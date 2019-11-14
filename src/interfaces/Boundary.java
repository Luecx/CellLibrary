package interfaces;

import core.Edge;
import core.Vertex;
import core.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class Boundary<T extends Boundary<T,C>, C extends Cell> implements Container{


    protected C cell;
    protected ArrayList<T> linked_boundaries;


    protected Vertex[] vertices;

    public Vertex getVertex(int id){
        return vertices[id];
    }
    public Vertex[] getVertices() {
        return vertices;
    }
    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public Boundary() {
        this.linked_boundaries = new ArrayList<>();
        this.linked_boundaries.add((T) this);
    }

    public void link(T e) {
        if(this.getLinked_boundaries().contains(e)) return;

        this.getLinked_boundaries().addAll(e.getLinked_boundaries());
        e.setLinked_boundaries(this.getLinked_boundaries());
    }

    public void setLinked_boundaries(ArrayList<T> linked_boundaries) {
        this.linked_boundaries = linked_boundaries;
    }

    public T getLinkedBoundary() {
        if(this.linked_boundaries.size() > 1){
            if(this.linked_boundaries.get(0) == this)
                return linked_boundaries.get(1);
            else{
                return linked_boundaries.get(0);
            }
        }
        return null;
    }

    public boolean hasLinkedBoundary(){
        if(this.linked_boundaries.size() > 1) return true;
        return false;
    }

    public T getLinkedBoundary(int index) {
        return linked_boundaries.get(index);
    }

    public ArrayList<T> getLinked_boundaries() {
        return linked_boundaries;
    }

    public C getCell() {
        return cell;
    }

    public void setCell(C cell) {
        this.cell = cell;
    }

    public abstract double integral();

    public abstract double orthogonalDistance(Vector3d vec);


    protected double[] data;

    @Override
    public void bindData(double[] data) {
        this.data = data;
    }

    @Override
    public double[] getData() {
        return data;
    }

    @Override
    public int dataSize() {
        return data.length;
    }
}
