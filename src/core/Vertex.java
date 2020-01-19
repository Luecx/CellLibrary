package core;

import core.vector.DenseVector;
import core.vector.Vector;
import core.vector.Vector2d;
import core.vector.Vector3d;
import interfaces.Boundary;
import interfaces.Container;

import java.util.Objects;

public class Vertex extends Boundary<Vertex, Edge> {

    Vector3d position;

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public void setX(double x) {
        this.position.setX(x);
    }

    public double getX() {
        return this.position.getX();
    }

    public void setY(double y) {
        this.position.setY(y);
    }

    public double getY() {
        return this.position.getY();
    }

    public void setZ(double z) {
        this.position.setZ(z);
    }

    public double getZ() {
        return this.position.getZ();
    }


    public Vertex() {
        this.position = new Vector3d();
    }

    public Vertex(Vector3d position) {
        this.position = position;
    }

    public Vertex(double x, double y, double z) {
        this.position = new Vector3d(x, y, z);
    }

    public Vertex(double x, double y) {
        this.position = new Vector3d(x, y, 0);
    }


    public boolean equals(Vertex other) {
        return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
    }


    @Override
    public double integral() {
        return 1;
    }

    @Override
    public double orthogonalDistance(Vector3d vec) {
        return vec.sub(position).length();
    }

    @Override
    public void prepare_data() {

    }

    @Override
    public void process_data() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(position, vertex.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "position=" + position +
                '}';
    }
}
