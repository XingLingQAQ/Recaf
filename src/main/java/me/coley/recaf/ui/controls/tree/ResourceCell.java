package me.coley.recaf.ui.controls.tree;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import me.coley.recaf.ui.controls.IconView;
import me.coley.recaf.util.UiUtil;
import me.coley.recaf.workspace.JavaResource;

/**
 * Cell renderer.
 *
 * @author Matt
 */
public class ResourceCell extends TreeCell {
	@Override
	@SuppressWarnings("unchecked")
	public void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);
		if(!empty) {
			Class<?> k = getTreeItem().getClass();
			Node g = null;
			String t = null;
			// Draw root
			if(k.equals(RootItem.class)) {
				t = getTreeItem().getValue().toString();
				g = new IconView(UiUtil.getResourceIcon((JavaResource) getTreeItem().getValue()));
			}
			// Draw root:classes
			else if(k.equals(ClassFolderItem.class)) {
				g = new IconView("icons/folder-source.png");
				BaseItem b = (BaseItem) getTreeItem();
				int count = b.resource().getClasses().size();
				t = String.format("classes (%d)", count);
			}
			// Draw root:files
			else if(k.equals(FileFolderItem.class)) {
				g = new IconView("icons/folder-resource.png");
				BaseItem b = (BaseItem) getTreeItem();
				int count = b.resource().getFiles().size();
				t = String.format("files (%d)", count);
			}
			// Draw classes
			else if(k.equals(ClassItem.class)) {
				ClassItem ci = (ClassItem) getTreeItem();
				g = ci.createGraphic();
				t = ci.getLocalName();
			}
			// Draw files
			else if(k.equals(FileItem.class)) {
				FileItem fi = (FileItem) getTreeItem();
				g = fi.createGraphic();
				t = fi.getLocalName();
			}
			// Draw directory/folders
			else if(k.equals(DirectoryItem.class)) {
				DirectoryItem di = (DirectoryItem) getTreeItem();
				g = new IconView("icons/class/package-flat.png");
				t = di.getLocalName();
			}
			setGraphic(g);
			setText(t);
		} else {
			setGraphic(null);
			setText(null);
		}
	}
}