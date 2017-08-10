package gui.visualgraph;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import dfa.framework.BlockState;
import dfa.framework.DFAExecution;
import gui.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    public static BufferedImage exportCurrentGraph(mxGraph graph, double scale, UIAbstractBlock selectedBlock, BlockState state) {
        BufferedImage graphImage = mxCellRenderer.createBufferedImage(graph, null, scale, null, true, null);

        int stateImageWidth = (int) (STATE_AREA_WIDTH * scale);
        BufferedImage stateImage = null;
        if (selectedBlock != null && state != null) {
            String in = state.getInState().getStringRepresentation();
            String out = state.getOutState().getStringRepresentation();
            int[] blockAndLineNumbers = selectedBlock.getBlockAndLineNumbers();

            stateImage  = new BufferedImage(stateImageWidth, graphImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gO = stateImage.createGraphics();
            gO.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (FONT_SIZE * scale)));
            gO.setColor(Colors.DARK_TEXT.getColor());

            String[] inStrings = in.split("\n");
            String[] outStrings = out.split("\n");
            int lineHeight = (int) (LINE_HEIGHT * scale);

            int height = lineHeight;
            String selectionString = "Selected Position: (" + blockAndLineNumbers[0];
            selectionString += blockAndLineNumbers[1] == -1 ? ")" : ", " + blockAndLineNumbers[1] + ")";

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

    public static ArrayList<BufferedImage> batchExport(DFAExecution dfa, double scale, boolean includeLineSteps) {
        dfa = dfa.clone();

        StatePanelOpen statePanel = new StatePanelOpen(null);
        statePanel.setSize(300, 1080);
        VisualGraphPanel panel = new VisualGraphPanel();
        panel.setJumpToAction(true);

        GraphUIController controller = new GraphUIController(panel);
        controller.setStatePanel(statePanel);
        controller.start(dfa);

        if (includeLineSteps) {
            return performLineBatchExport(dfa, controller, panel, scale);
        }

        return performBlockBatchExport(dfa, controller, panel, scale);
    }

    private static ArrayList<BufferedImage> performBlockBatchExport(DFAExecution dfa, GraphUIController controller,
                                                                    VisualGraphPanel panel, double scale) {
        ArrayList<BufferedImage> result = new ArrayList<>();

        for (int blockStep = 0; blockStep < dfa.getTotalBlockSteps(); blockStep++) {
            dfa.setCurrentBlockStep(blockStep);
            controller.refresh();

            UIAbstractBlock selectedBlock = controller.getSelectedBlock();
            BlockState state = selectedBlock == null ? null : dfa.getCurrentAnalysisState().getBlockState(selectedBlock.getDFABlock());

            result.add(exportCurrentGraph(panel.getMxGraph(), scale, selectedBlock, state));
        }

        return result;
    }

    private static ArrayList<BufferedImage> performLineBatchExport(DFAExecution dfa, GraphUIController controller,
                                                                   VisualGraphPanel panel, double scale) {
        ArrayList<BufferedImage> result = new ArrayList<>();

        for (int lineStep = 0; lineStep < dfa.getTotalElementarySteps(); lineStep++) {
            dfa.setCurrentElementaryStep(lineStep);
            controller.refresh();

            UIAbstractBlock selectedBlock = controller.getSelectedBlock();
            BlockState state = selectedBlock == null ? null : dfa.getCurrentAnalysisState().getBlockState(selectedBlock.getDFABlock());

            result.add(exportCurrentGraph(panel.getMxGraph(), scale, selectedBlock, state));
        }

        return result;
    }
}
