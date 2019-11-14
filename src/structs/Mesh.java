package structs;

import core.Edge;
import core.Face;
import core.Vertex;
import core.Volume;

import java.util.ArrayList;
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


    public abstract V new_vertex(double x, double y, double z);

    public abstract E new_edge(V v1, V v2);

    public abstract F new_face(E... edges);

    public abstract K new_volume(Face... faces);



}
