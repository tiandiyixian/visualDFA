package gui.visualgraph;

import com.mxgraph.util.mxConstants;

/**
 * Helper class containing style constants for {@code mxGraph}.
 * 
 * @author Patrick Petrovic
 */
public class Styles {
    public final static int BLOCK_WIDTH = 275;
    public final static int LINE_HEIGHT = 20;
    public final static int BREAKPOINT_PADDING = 5;
    public final static int BREAKPOINT_SIZE = 10;

    public final static String INITIAL_COLOR = "#eff7fb";
    public final static String BREAKPOINT_COLOR = "#dd7063";
    public final static String CURRENT_COLOR = "#d0febf";
    public final static String ON_WORKLIST_COLOR = "#fff7a9";
    public final static String VISITED_COLOR = "#c6dafd";
    public final static String TRANSPARENT_COLOR = "rgba(255, 255, 255, 0)";
    public final static String HIGHLIGHT_COLOR = "rgba(255, 255, 255, 0.4)";

    public final static String NO_BORDER = "strokeColor=none;";
    public final static String SELECTION_BORDER_COLOR = "#1f475e";
    public final static float SELECTION_STROKE_WIDTH = 2.0F;
    public final static float DEFAULT_STROKE_WIDTH = 1.0F;

    public final static String TEXT_ALIGN_LEFT = mxConstants.STYLE_ALIGN + "=" + mxConstants.ALIGN_LEFT + ";";
    public final static int TEXT_SIZE = 12;
    public final static String TEXT_COLOR = mxConstants.STYLE_FONTCOLOR + "=rgb(17, 37, 48);";
}
