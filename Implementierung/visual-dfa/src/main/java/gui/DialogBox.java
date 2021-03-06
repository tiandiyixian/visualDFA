package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Superclass for different {@code DialogBox}es. Includes a title, a
 * contentPanel and a buttonPane. Can be customized by subclasses. Layout and
 * content of contentPanel and buttonPane have to be set by subclasses. Blocks
 * user input on top-level windows.
 * 
 * @author Michael
 *
 */

public abstract class DialogBox extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private final JPanel borderPanel = new JPanel();
    protected final JPanel contentPanel = new JPanel();
    protected final JPanel buttonPane = new JPanel();
    private final JPanel titlePane = new JPanel();

    /**
     * Create the dialog. Set owner and make modal, so that top-level windows
     * block user Input.
     * 
     * @param owner
     *            the {@code Frame}, which is the owner of this
     *            {@code DialogBox}
     * 
     * @see Frame
     *
     */
    public DialogBox(Frame owner) {
        super(owner, true);

    }

    /**
     * Create the dialog. Set owner and make modal, so that top-level windows
     * block user Input.
     * 
     * @param owner
     *            the {@code Dialog} which is the owner of this
     *            {@code DialogBox}
     * 
     * @see Dialog
     *
     */
    public DialogBox(Dialog owner) {
        super(owner, true);

    }

    /**
     * Initialize the titlePane, contentPanel and the buttonPane. Automatically
     * size the {@code DialogBox}.
     * 
     * @param title
     *            the title of the {@code DialogBox}
     */
    protected final void init(String title) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setResizable(false);
        borderPanel.setLayout(new BorderLayout());
        borderPanel.setBackground(Colors.BACKGROUND.getColor());
        borderPanel.setBorder(
                new CompoundBorder(new LineBorder(Colors.GREY_BORDER.getColor(), 2), new EmptyBorder(5, 5, 5, 5)));

        getContentPane().add(borderPanel);

        initTitlePane(title);
        borderPanel.add(titlePane, BorderLayout.NORTH);

        initContentPanel();
        borderPanel.add(contentPanel, BorderLayout.CENTER);

        initButtonPane();
        borderPanel.add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

    }

    /**
     * Set the layout and content of the titlePane.
     * 
     * @param title
     *            the title, which is displayed in the titlePane
     */
    private void initTitlePane(String title) {
        titlePane.setBackground(Colors.BACKGROUND.getColor());
        JLabelDecorator lblDecorator = new JLabelDecorator(new JComponentDecorator());
        JLabel lbl_title = new JLabel();
        lblDecorator.decorateTitle(lbl_title, title);
        titlePane.add(lbl_title);
    }

    /**
     * Is to be implemented by subclasses. Set the layout and content of the
     * contentPanel.
     */
    protected abstract void initContentPanel();

    /**
     * Is to be implemented by subclasses. Set the layout and content of the
     * buttonPane.
     */
    protected abstract void initButtonPane();

}
