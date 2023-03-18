package genepi.genomic.utils.commands.gwas.util;

import java.util.HashMap;
import java.util.Map;

public class PlotlyUtil {

	public static Map<String, Object> createHorizontalLine(double y, String color, int width, String style) {

		Map<String, Object> shape = new HashMap<String, Object>();
		shape.put("type", "line");
		shape.put("xref", "paper");
		shape.put("x0", 0);
		shape.put("x1", 1);
		shape.put("y0", y);
		shape.put("y1", y);

		Map<String, Object> lineStyle = new HashMap<>();
		lineStyle.put("color", color);
		lineStyle.put("width", width);
		lineStyle.put("dash", style);
		shape.put("line", lineStyle);

		return shape;
	}

	public static Map<String, Object> createLine(double x0, double y0, double x1, double y1, String color, int width) {

		Map<String, Object> shape = new HashMap<String, Object>();
		shape.put("type", "line");
		shape.put("xref", "x");
		shape.put("yref", "y");
		shape.put("x0", x0);
		shape.put("y0", y0);
		shape.put("x1", x1);
		shape.put("y1", y1);

		Map<String, Object> lineStyle = new HashMap<>();
		lineStyle.put("color", color);
		lineStyle.put("width", width);
		lineStyle.put("linecap", "round");
		shape.put("line", lineStyle);

		return shape;
	}
}
