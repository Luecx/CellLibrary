package structs;

import core.Edge;
import core.Face;
import core.Vertex;
import core.Volume;
import interfaces.Boundary;
import sun.misc.IOUtils;
import tools.Loader;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Mesh<V extends Vertex, E extends Edge, F extends Face, K extends Volume> {

    protected ArrayList<V> vertices = new ArrayList<>();
    protected ArrayList<E> edges = new ArrayList<>();
    protected ArrayList<F> faces = new ArrayList<>();
    protected ArrayList<K> volumes = new ArrayList<>();

    protected HashSet<K> hash_volumes = new HashSet<>();
    protected HashSet<V> hash_vertices = new HashSet<>();
    protected HashSet<E> hash_edges = new HashSet<>();
    protected HashSet<F> hash_faces = new HashSet<>();

    public Mesh() {
    }

    public Mesh(ArrayList<F> faces) {
        for (F f : faces) {
            this.addFace(f);
        }
    }

    public Mesh(ArrayList<V> vertices, ArrayList<E> edges, ArrayList<F> faces) {
        this.vertices = vertices;
        this.edges = edges;
        this.faces = faces;

        this.hash_edges.addAll(edges);
        this.hash_faces.addAll(faces);
        this.hash_vertices.addAll(vertices);
    }

    public Mesh(Face... faces) {
        for (Face f : faces) {
            this.addFace((F)f);
        }
    }

    public Mesh(ArrayList<V> vertices, ArrayList<E> edges, ArrayList<F> faces, ArrayList<K> volumes) {
        this(vertices, edges, faces);
        this.volumes = volumes;
        this.hash_volumes.addAll(volumes);
    }

    public Mesh(K... volumes) {
        for (K v : volumes) {
            this.addVolume(v);
        }
    }

    public void addFace(F face) {
        if (!hash_faces.contains(face)) {
            hash_faces.add(face);
            faces.add(face);
        } else {
            return;
        }

        for (Edge e : face.getBoundaries()) {
            if (!hash_edges.contains(e)) {
                hash_edges.add((E) e);
                edges.add((E) e);
            }
        }

        for (Vertex e : face.getVertices()) {
            if (!hash_vertices.contains(e)) {
                hash_vertices.add((V)e);
                vertices.add((V)e);
            }
        }

    }

    public void addVolume(K v) {
        if (!hash_volumes.contains(v)) {
            volumes.add(v);
            hash_volumes.add(v);
            for (Face f : v.getBoundaries()) {
                this.addFace((F) f);
            }
        }
    }

    public ArrayList<V> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<V> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<E> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<E> edges) {
        this.edges = edges;
    }

    public ArrayList<F> getFaces() {
        return faces;
    }

    public void setFaces(ArrayList<F> faces) {
        this.faces = faces;
    }

    public ArrayList<K> getVolumes() {
        return volumes;
    }

    public void setVolumes(ArrayList<K> volumes) {
        this.volumes = volumes;
    }

    public void prepare_data() {
        for (Vertex v : vertices) {
            v.prepare_data();
        }
        for (Edge e : edges) {
            e.prepare_data();
        }
        for (Face f:faces){
            f.prepare_data();
        }
        for (Volume v : volumes) {
            v.prepare_data();
        }
    }

    public void process_data() {
        for (Vertex v : vertices) {
            v.process_data();
        }
        for (Edge e : edges) {
            e.process_data();
        }
        for (Face f:faces){
            f.process_data();
        }
        for (Volume v : volumes) {
            v.process_data();
        }
    }

    @Override
    public int hashCode() {
        int code = 0;
        code += vertices.size();
        code += edges.size();
        code += faces.size();
        code += volumes.size();

        for(Edge e:edges){
            code += e.getLinked_boundaries().size();
        }
        for(Face e:faces){
            code += e.getLinked_boundaries().size();
        }

        return code;
    }

    public <T extends Mesh> T copyToType(T o){
        HashMap<Vertex, Integer> vertexHashMap = new HashMap<>();
        HashMap<Edge, Integer> edgeHashMap = new HashMap<>();
        HashMap<Face, Integer> faceHashMap = new HashMap<>();


        ArrayList<Vertex> new_vertices = new ArrayList<>();
        ArrayList<Edge> new_edges = new ArrayList<>();
        ArrayList<Face> new_faces = new ArrayList<>();
        ArrayList<Volume> new_volumes = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            vertexHashMap.put(v, i);
            new_vertices.add(new_vertex(v.getX(), v.getY(), v.getZ()));
        }
        for (int i = 0; i < edges.size(); i++) {
            Edge v = edges.get(i);
            edgeHashMap.put(v, i);
            new_edges.add(o.new_edge(
                    new_vertices.get(vertexHashMap.get(v.getV1())),
                    new_vertices.get(vertexHashMap.get(v.getV2()))
            ));
        }
        for (int i = 0; i < faces.size(); i++) {
            Face v = faces.get(i);
            faceHashMap.put(v, i);

            ArrayList<Edge> eg = new ArrayList<>();
            for (Boundary e : v.getBoundaries()) {
                eg.add(new_edges.get(edgeHashMap.get(e)));
            }

            new_faces.add(o.new_face(eg.toArray(o.empty_edge_array())));
        }
        for (Volume v : volumes) {
            ArrayList<Face> eg = new ArrayList<>();
            for (Boundary e : v.getBoundaries()) {
                eg.add(new_faces.get(faceHashMap.get(e)));
            }
            new_volumes.add(o.new_volume(eg.toArray(o.empty_face_array())));
        }

        HashSet<Edge> hashedEdges = new HashSet<>();
        for (Edge e : edges) {
            ArrayList<Edge> linked = e.getLinked_boundaries();
            if (!hashedEdges.contains(linked.get(0))) {
                hashedEdges.addAll(linked);
                for (int i = 1; i < linked.size(); i++) {

                    Edge e1 = new_edges.get(
                            edgeHashMap.get(linked.get(0)));
                    Edge e2 = new_edges.get(
                            edgeHashMap.get(linked.get(i)));

                    e1.link(e2);
                }
            }
        }
        HashSet<Face> hashedFaces = new HashSet<>();
        for (Face fc : faces) {
            ArrayList<Face> linked = fc.getLinked_boundaries();
            if (!hashedFaces.contains(linked.get(0))) {
                for (int i = 1; i < linked.size(); i++) {

                    Face e1 = new_faces.get(
                            faceHashMap.get(linked.get(0)));
                    Face e2 = new_faces.get(
                            faceHashMap.get(linked.get(i)));

                    e1.link(e2);
                }
            }
        }
        T m = (T)o.new_mesh();
        m.vertices = new_vertices;
        m.edges = new_edges;
        m.volumes = new_volumes;
        m.faces = new_faces;
        return m;
    }

    public Mesh<V,E,F,K> copy(){
        return copyToType(this);
    }

    public abstract Mesh<V,E,F,K> new_mesh();

    public abstract V new_vertex(double x, double y, double z);

    public abstract E new_edge(V v1, V v2);

    public abstract F new_face(E... edges);

    public abstract K new_volume(F... faces);

    public abstract F[] empty_face_array();

    public abstract E[] empty_edge_array();





}
