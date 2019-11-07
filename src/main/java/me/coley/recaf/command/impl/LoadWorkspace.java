package me.coley.recaf.command.impl;

import me.coley.recaf.command.completion.*;
import me.coley.recaf.workspace.*;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import static me.coley.recaf.util.Log.*;

/**
 * Command for loading the a workspace from a file.
 *
 * @author Matt
 */
@CommandLine.Command(name = "loadworkspace", description = "Loads a workspace from a resource or workspace config.")
public class LoadWorkspace implements Callable<Workspace> {
	@CommandLine.Parameters(index = "0",  description = "The file to load. " +
			"Supported types are: class, jar, json", completionCandidates = WorkspaceFileCompletions.class)
	public File input;
	@CommandLine.Option(names = { "--sources" },  description = "Archive containing sources of the resource.",
			completionCandidates = ArchiveFileCompletions.class)
	public File sources;
	@CommandLine.Option(names = { "--docs" },  description = "Archive containing javadocs of the resource.",
			completionCandidates = ArchiveFileCompletions.class)
	public File javadoc;
	@CommandLine.Option(names = { "--lazy" },  description = "Don't immediately load the workspace content.")
	public boolean lazy;
	@CommandLine.Option(names = "--skip")
	public List<String> skippedPrefixes;

	@Override
	public Workspace call() throws Exception {
		String name = input.getName().toLowerCase();
		String ext = name.substring(name.lastIndexOf(".") + 1);
		JavaResource resource = null;
		switch(ext) {
			case "class":
				resource = new ClassResource(input);
				break;
			case "jar":
				resource = new JarResource(input);
				break;
			case "json":
				// Represents an already existing workspace, so we can parse and return that here
				Workspace workspace = null;
				try {
					workspace = WorkspaceIO.fromJson(input);
				} catch(Exception ex) {
					throw new IllegalArgumentException("Failed to parse workspace config '" + name + "'", ex);
				}
				// Initial load classes & files
				if (!lazy) {
					workspace.getPrimary().getClasses();
					workspace.getPrimary().getFiles();
				}
				info("Loaded workspace from: {}", input.getName());
				return workspace;
			default:
				throw new IllegalArgumentException("Unsupported file type '" + ext + "'");
		}
		//
		if (skippedPrefixes != null)
			resource.setSkippedPrefixes(skippedPrefixes);
		// Initial load classes & files
		if (!lazy) {
			resource.getClasses();
			resource.getFiles();
		}
		// Load sources/javadoc if present
		if (sources != null && sources.isFile())
			resource.setClassSources(sources);
		if (javadoc != null && javadoc.isFile())
			resource.setClassDocs(javadoc);
		info("Loaded workspace from: {}", input.getName());
		return new Workspace(resource);
	}
}
