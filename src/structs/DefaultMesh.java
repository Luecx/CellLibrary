package structs;

import core.Edge;
import core.Face;
import core.Vertex;
import core.Volume;

import java.util.ArrayList;

public class DefaultMesh extends Mesh<Vertex, Edge, Face, Volume> {

    public DefaultMesh() {
    }

    public DefaultMesh(ArrayList<Face> faces) {
        super(faces);
    }

    public DefaultMesh(ArrayList<Vertex> vertices, ArrayList<Edge> edges, ArrayList<Face> faces) {
        super(vertices, edges, faces);
    }

    public DefaultMesh(Face... faces) {
        super(faces);
    }

    public DefaultMesh(ArrayList<Vertex> vertices, ArrayList<Edge> edges, ArrayList<Face> faces, ArrayList<Volume> volumes) {
        super(vertices, edges, faces, volumes);
    }

    public DefaultMesh(Volume... volumes) {
        super(volumes);
    }

    @Override
    public Mesh<Vertex, Edge, Face, Volume> new_mesh() {
        return new DefaultMesh();
    }

    @Override
    public Vertex new_vertex(double x, double y, double z) {
        return new Vertex(x,y,z);
    }

    @Override
    public Edge new_edge(Vertex v1, Vertex v2) {
        return new Edge(v1,v2);
    }

    @Override
    public Face new_face(Edge... edges) {
        return new Face(edges);
    }

    @Override
    public Volume new_volume(Face... faces) {
        return new Volume(faces);
    }

    @Override
    public Face[] empty_face_array() {
        return new Face[0];
    }

    @Override
    public Edge[] empty_edge_array() {
        return new Edge[0];
    }
}
