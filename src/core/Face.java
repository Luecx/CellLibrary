package core;

import core.vector.Vector3d;
import interfaces.Boundary;
import interfaces.Cell;
import tools.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class Face extends Cell<Edge, Face, Volume> {


    public Face() {
    }

    public Face(Face other) {
        super();

        Edge[] ar = new Edge[other.boundaries.length];
        for (int i = 0; i < other.boundaries.length; i++) {
            //System.out.println(edges[i].getCell());
            if (other.boundaries[i].getCell() != null) {
                ar[i] = new Edge(other.boundaries[i]);
            } else {
                ar[i] = other.boundaries[i];
            }
            ar[i].setCell(this);
        }

        this.setBoundaries(ar);
        this.setVertices(other.getVertices());
        this.link(other);
    }

    public Face(Edge... edges) {
        super();

        HashMap<Vertex, Integer> count = Tools.count(edges);
        for (Vertex v : count.keySet()) {
            if (count.get(v) != 2) {
                throw new RuntimeException("Vertex " + v + " has not been found twice!");
            }
        }

        Vertex[] vertices = new Vertex[edges.length];
        for(int i = 0; i < edges.length; i++){
            vertices[i] = edges[i].getV1();
            if(edges[i].getV2() != edges[(i+1) % edges.length].getV1()){
                throw new RuntimeException("Edges do not form a closed loop!");
            }
        }
        this.setVertices(vertices);

        if (!Tools.vectorsAreInPlane(this.getVertices())) {
            throw new RuntimeException("Vertices are not in a 3d-plane!");
        }

        Edge[] ar = new Edge[edges.length];

        for (int i = 0; i < edges.length; i++) {
            //System.out.println(edges[i].getCell());
            if (edges[i].getCell() != null) {
                ar[i] = new Edge(edges[i]);
            } else {
                ar[i] = edges[i];
            }
            ar[i].setCell(this);
        }

        this.setBoundaries(ar);
    }

    @Override
    public boolean inside(Vector3d vec) {
        return false;
    }


    @Override
    public double integral() {

        double t = 0;

        Vector3d center = this.center();

        for (Boundary e : this.getBoundaries()) {
            t += 0.5 * ((Edge) e).area(center);
        }
        return t;

//        Vertex[] vertices = this.getVertices();
//        System.out.println(Arrays.toString(vertices));
//
//        double t = 0;
//        for(int i = 1 ; i < vertices.length-1; i++){
//            double d1 = vertices[i].sub(vertices[0]).length();
//            double d2 = vertices[i+1].sub(vertices[0]).length();
//            double d3 = vertices[i].self_add(vertices[i+1]).length();
//            double s = 0.5 * (d1 + d2 + d3);
//            t += Math.sqrt(Math.abs(s*(s-d1)*(s-d2)*(s-d3)));
//        }
//        return t;
    }

    @Override
    public double orthogonalDistance(Vector3d vec) {
        return Tools.orthog_dist(getVertices()[0].position, getVertices()[1].position, getVertices()[2].position, vec);
    }

    @Override
    public void prepare_data() {

    }

    @Override
    public void process_data() {

    }

}
