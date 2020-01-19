package core;

import core.vector.Vector3d;
import interfaces.Cell;
import tools.Tools;

import java.util.Arrays;
import java.util.Objects;

public class Edge extends Cell<Vertex, Edge, Face> {

    public Edge() {
    }

    public Edge(Edge other) {
        super();
        this.setVertices(other.getVertices());
        this.setBoundaries(other.getVertices());
        this.link(other);
    }

    public Edge(Vertex v1, Vertex v2) {
        super();
        this.setVertices(new Vertex[]{v1, v2});
        this.setBoundaries(this.vertices);
    }

    @Override
    public double integral() {
        return getV1().getPosition().sub(
                getV2().getPosition()).length();
    }

    public void swapVertices() {
        Vertex v1 = getV1();
        this.vertices[0] = this.vertices[1];
        this.vertices[1] = v1;
    }

    public Vertex getV1() {
        return getVertices()[0];
    }

    public Vertex getV2() {
        return getVertices()[1];
    }

    public double area(Vector3d other) {
        return other.sub(getV2().position).cross(other.sub(getV1().position)).length();
    }

    @Override
    public double orthogonalDistance(Vector3d vec) {
        Vector3d cross = getV1().position.sub(getV2().position).cross(vec.sub(getV2().position));
        return Tools.orthog_dist(getV1().position,
                getV2().position,
                getV1().position.add(cross),
                vec);
    }

    @Override
    public void prepare_data() {

    }

    @Override
    public void process_data() {

    }

    @Override
    public boolean inside(Vector3d vec) {
        return false;
    }



    @Override
    public String toString() {
        return "Edge{" +getV1().getPosition() + ", " + getV2().getPosition() + "}";
    }
}
