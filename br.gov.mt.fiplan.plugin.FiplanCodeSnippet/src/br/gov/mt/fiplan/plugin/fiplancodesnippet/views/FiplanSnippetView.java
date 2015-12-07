package br.gov.mt.fiplan.plugin.fiplancodesnippet.views;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;


public class FiplanSnippetView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.plugin.study.views.SampleView";

	private TreeViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	private Map<String, String[]> tags = new HashMap<String, String[]>();

	{
		tags.put("HTML", new String[]{"<body>\n</body>", "<input type=\"text\" name=\"name\"/>", "<a href=\"\">Link</a>"});
		tags.put("jQuery", new String[]{
				"$(\"id\").val()",
				"	$(document).ready(function(){\n" + 
				"	//TODO\n" + 
				"	});",
				"Localizar","Replace","Formatar","Refatorar"});		
	}
	 
	class ViewContentProvider extends LabelProvider implements ITreeContentProvider, IStyledLabelProvider {
		public String getText(Object element){
			if(element instanceof Map){
				return "Menu";
			} else if(element instanceof Map.Entry){
				return ((Map.Entry) element).getKey().toString();				
			} else if(element instanceof String){
				return element.toString();
			} else {
				return "Elemento desconhecido: " + element.getClass();
			}
		}
		

		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getChildren(Object parent) {
			if(parent instanceof Map){
				return ((Map)parent).entrySet().toArray();
			} else if(parent instanceof Map.Entry){
				return getChildren(((Map.Entry)parent).getValue());
			} else if(parent instanceof String[]){
				return (String[])parent;
			} else {
				return new String[0];
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Object[]){
				return ((Object[]) inputElement);
			} else {
				return new Object[0];
			}
		}

		@Override
		public Object getParent(Object arg0) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof Map){
				return !((Map)element).isEmpty();
			} else if(element instanceof Map.Entry){
				return hasChildren(((Map.Entry)element).getValue());
			} else if(element instanceof String[]){
				return (element != null && ((String[])element).length > 0);
			} else {
				return false;
			}
		}


		@Override
		public Image getImage(Object element) {
			if(element instanceof Map.Entry){
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			} else 
				return super.getImage(element);
		}


		@Override
		public StyledString getStyledText(Object element) {
			String text = getText(element);
			StyledString ss = new StyledString(text);
			if(element instanceof String) {
				ss.append("", StyledString.DECORATIONS_STYLER);
			}
			return ss;
		}
		
		
	}
	
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public FiplanSnippetView() {
	}

	public void createPartControl(Composite parent) {		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new ViewContentProvider()));
		//viewer.setSorter(new NameSorter());
		viewer.setInput(new Object[]{tags});
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeSelection selection = (TreeSelection)event.getSelection();				
				Object obj = selection.getFirstElement();
				
				try {
					ITextSelection content = (ITextSelection)
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActiveEditor().getEditorSite().getSelectionProvider()
						.getSelection();
					
					IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().getActiveEditor();
					ITextEditor editor = (ITextEditor)part;
					IDocumentProvider provider = editor.getDocumentProvider();
					IDocument document = provider.getDocument(editor.getEditorInput());
					ISelection sel = editor.getSelectionProvider().getSelection();
					
					if(sel instanceof TextSelection) {
						final TextSelection text = (TextSelection)sel;
						try {							
							document.replace( text.getOffset(), text.getLength(), obj.toString() );
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Create the help context id for the viewer's control
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "br.com.plugin.Study.viewer");
//		getSite().setSelectionProvider(viewer);
//		makeActions();
//		hookContextMenu();
//		hookDoubleClickAction();
//		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				FiplanSnippetView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
