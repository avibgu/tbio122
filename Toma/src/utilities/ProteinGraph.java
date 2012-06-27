package utilities;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import org.jgrapht.graph.DefaultEdge;

public class ProteinGraph extends JApplet {

	private static final long serialVersionUID = 3256444702936019250L;

	private static final Color DEFAULT_BG_COLOR = Color.WHITE;

	private static final Color H_COLOR = Color.BLUE;
	private static final Color P_COLOR = Color.RED;

	private static final int SCALE_FACTOR = 50;
	private static final int BOUNDRY = 25;

	private static final Dimension DEFAULT_SIZE = new Dimension(1200,600);

	private static final double MONOMER_HEIGHT = 15;
	private static final double MONOMER_WIDTH = 15;


	private JGraphModelAdapter<String, DefaultEdge> jgAdapter;

	public static void show(String pResult) {

		ProteinGraph applet = new ProteinGraph();

		applet.init(pResult);

		JFrame frame = new JFrame();

		frame.getContentPane().add(applet);
		frame.setTitle(pResult.split("\n")[1]);		//TODO: improve..
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public void init(String pResult) {

		String[] stringMonomers = pResult.split("\n");

		// create a JGraphT graph
		ListenableGraph<String, DefaultEdge> g = new ListenableDirectedMultigraph<String, DefaultEdge>(
				DefaultEdge.class);

		// create a visualization using JGraph, via an adapter
		jgAdapter = new JGraphModelAdapter<String, DefaultEdge>(g);

		JGraph jgraph = new JGraph(jgAdapter);

		adjustDisplaySettings(jgraph);

		getContentPane().add(jgraph);

		resize(DEFAULT_SIZE);

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;

		for (int i = 3; i < stringMonomers.length; i++){

			String[] monomer = stringMonomers[i].split(",");

			int x = Integer.parseInt(monomer[0]);
			int y = Integer.parseInt(monomer[1]);

			if (x < minX)
				minX = x;

			if (y < minY)
				minY = y;
		}

		String prevCell = "";

		for (int i = 3; i < stringMonomers.length; i++) {

			String[] monomer = stringMonomers[i].split(",");

			String currCell = String.valueOf(i) + monomer[2];

			g.addVertex(currCell);

			if (3 != i)
				removeText(g.addEdge(prevCell, currCell));

			positionVertexAt(currCell, (Integer.parseInt(monomer[0]) - minX)
					* SCALE_FACTOR + BOUNDRY, (Integer.parseInt(monomer[1]) - minY)
					* SCALE_FACTOR + BOUNDRY);

			prevCell = currCell;
		}
	}

	private void adjustDisplaySettings(JGraph jg) {

		jg.setPreferredSize(DEFAULT_SIZE);

		Color c = DEFAULT_BG_COLOR;
		String colorStr = null;

		try {
			colorStr = getParameter("bgcolor");
		}

		catch (Exception e) {
		}

		if (colorStr != null)
			c = Color.decode(colorStr);

		jg.setBackground(c);
	}

	@SuppressWarnings("unchecked")
	private void removeText(DefaultEdge pEdge) {

		DefaultGraphCell cell = jgAdapter.getEdgeCell(pEdge);

		AttributeMap attr = cell.getAttributes();

		GraphConstants.setValue(attr, "");

		AttributeMap cellAttr = new AttributeMap();

		cellAttr.put(cell, attr);

		jgAdapter.edit(cellAttr, null, null, null);
	}

	@SuppressWarnings("unchecked")
	private void positionVertexAt(Object vertex, int x, int y) {

		DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);

		AttributeMap attr = cell.getAttributes();

		GraphConstants.setBounds(attr, new Rectangle2D.Double(x, y,
				MONOMER_WIDTH, MONOMER_HEIGHT));

		if (((String)vertex).contains("H"))
			GraphConstants.setBackground(attr, H_COLOR);

		else
			GraphConstants.setBackground(attr, P_COLOR);

		GraphConstants.setValue(attr, "");

		AttributeMap cellAttr = new AttributeMap();

		cellAttr.put(cell, attr);

		jgAdapter.edit(cellAttr, null, null, null);
	}

	private static class ListenableDirectedMultigraph<V, E> extends
			DefaultListenableGraph<V, E> implements DirectedGraph<V, E> {

		private static final long serialVersionUID = 1L;

		ListenableDirectedMultigraph(Class<E> edgeClass) {
			super(new DirectedMultigraph<V, E>(edgeClass));
		}
	}
}