package structs;

import core.Edge;
import core.Face;
import core.Vertex;

public class Triangle extends Face{

    public Triangle(Triangle other) {
        super(other);
    }

    public Triangle(Edge... edges) {
        super(edges);
        if(edges.length != 3) throw new RuntimeException("Triangle expects 3 edges!");
    }

    public Triangle(Edge e, Vertex v){
        super(new Edge(v, e.getV1()), new Edge(e),new Edge(e.getV2(), v));
    }

    public Vertex getV1(){
        return getVertex(0);
    }

    public Vertex getV2(){
        return getVertex(0);
    }

    public Vertex getV3(){
        return getVertex(0);
    }

}
