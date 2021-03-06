package gui.visualgraph;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import dfa.framework.BlockState;
import dfa.framework.DFAExecution;
import dfa.framework.LatticeElement;
import gui.Colors;

/**
 * Responsible for exporting the graph to {@code BufferedImage}.
 *
 * @author Patrick Petrovic
 */
public class GraphExporter {
    private static final int PADDING = 20;
    private static final int LINE_HEIGHT = 15;
    private static final int STATE_AREA_WIDTH = 225;
    private static final int FONT_SIZE = 12;

    /**
     * Exports the given graph to {@code BufferedImage} and renders the selected block state into it, if given.
     *
     * @param graph
     *         the {@code mxGraph} to export
     * @param scale
     *         the scale of the image (usually 1.0, 2.0 or 3.0)
     * @param selectedBlock
     *         the selected {@code UIAbstractBlock} (may be {@code null})
     * @param state
     *         the {@code BlockState} of the selected block, will be rendered (may be {@code null})
     *
     * @return a {@code BufferedImage} containing the graph and the selected block state
     */
    public BufferedImage exportCurrentGraph(mxGraph graph, double scale, UIAbstractBlock selectedBlock, BlockState<? extends LatticeElement> state) {
        BufferedImage graphImage;

        int stateImageWidth = (int) (STATE_AREA_WIDTH * scale);
        BufferedImage stateImage = null;
        if (selectedBlock != null && state != null) {
            // Selection strokes are not rendered by createBufferedImage(), so we fake them using a border.
            // First step: Get the current border color, so we can reset it after creating the BufferedImage.
            mxCell currentCell = selectedBlock.getMxCell();
            String originalStrokeColor = (String) graph.getCellStyle(currentCell).get(mxConstants.STYLE_STROKECOLOR);

            // Second step: Add selection border, create BufferedImage, then reset border.
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, Styles.SELECTION_BORDER_COLOR, new Object[]{currentCell});
            graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, String.valueOf(Styles.SELECTION_STROKE_WIDTH), new Object[]{selectedBlock.getMxCell()});
            graphImage = mxCellRenderer.createBufferedImage(graph, null, scale, null, true, null);
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, originalStrokeColor == null ? mxConstants.NONE : originalStrokeColor, new Object[]{currentCell});
            graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, String.valueOf(Styles.DEFAULT_STROKE_WIDTH), new Object[]{currentCell});

            LatticeElement inState = state.getInState();
            LatticeElement outState = state.getOutState();
            String in = inState == null ? "<not set>" : inState.getStringRepresentation();
            String out = outState == null ? "<not set>" : outState.getStringRepresentation();

            int blockNumber = selectedBlock.getBlockNumber();
            int lineNumber = selectedBlock.getLineNumber();

            stateImage = new BufferedImage(stateImageWidth, graphImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gO = stateImage.createGraphics();
            gO.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (FONT_SIZE * scale)));
            gO.setColor(Colors.DARK_TEXT.getColor());

            String[] inStrings = in.split("\n");
            String[] outStrings = out.split("\n");
            int lineHeight = (int) (LINE_HEIGHT * scale);

            int height = lineHeight;
            String selectionString = "Selected Position: (" + blockNumber;
            selectionString += lineNumber == -1 ? ")" : ", " + lineNumber + ")";

            gO.drawString(selectionString, 0, height);

            height += 2 * lineHeight;
            gO.drawString("In State:", 0, height);

            for (String s : inStrings) {
                height += lineHeight;
                gO.drawString(s, 0, height);
            }

            height += 3 * lineHeight;
            gO.drawString("Out State:", 0, height);

            for (String s : outStrings) {
                height += lineHeight;
                gO.drawString(s, 0, height);
            }
        } else {
            graphImage = mxCellRenderer.createBufferedImage(graph, null, scale, null, true, null);
        }

        BufferedImage result = new BufferedImage(graphImage.getWidth() + stateImageWidth + 2 * PADDING, graphImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics resultGraphics = result.getGraphics();
        resultGraphics.drawImage(graphImage, PADDING, 0, null);
        if (stateImage != null) {
            resultGraphics.drawImage(stateImage, graphImage.getWidth() + 2 * PADDING, 0, null);
        }
        resultGraphics.dispose();

        return result;
    }

    public void batchExportAsync(DFAExecution<? extends LatticeElement> dfa, double scale, boolean includeLineSteps, GraphExportCallback callback) {
        dfa = dfa.clone();

        VisualGraphPanel panel = new VisualGraphPanel();
        panel.setJumpToAction(true);

        GraphUIController controller = new GraphUIController(panel);
        controller.start(dfa);

        int totalSteps = includeLineSteps ? dfa.getTotalElementarySteps() : dfa.getTotalBlockSteps();
        callback.setMaxStep(totalSteps + 1);

        for (int step = 0; step < totalSteps; step++) {
            if (includeLineSteps) {
                dfa.setCurrentElementaryStep(step);
            } else {
                dfa.setCurrentBlockStep(step);
            }
            controller.refresh();

            UIAbstractBlock selectedBlock = panel.getSelectedBlock();
            BlockState<? extends LatticeElement> state = selectedBlock == null ? null : dfa.getCurrentAnalysisState().getBlockState(selectedBlock.getDFABlock());

            callback.onImageExported(exportCurrentGraph(panel.getMxGraph(), scale, selectedBlock, state));
            callback.setExportStep(step);
        }

        callback.done();
    }
}
