package tools;

import core.Edge;
import core.Face;
import core.Vertex;
import core.Volume;
import core.vector.Vector2d;
import core.vector.Vector3d;
import structs.DefaultMesh;
import structs.Mesh;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Transformer {


    /**
     * this method creates the voronoi diagram of 2d meshes
     * that only consist of triangles. It is mainly used for
     * Finite Volume discretisations.
     *
     * The method binds the center position of each new face
     * to the face itself.
     *
     * @param mesh
     * @param <T>
     * @return
     */
    public static <T extends Mesh> T generateNodeCenteredMesh(T mesh) {

        HashMap<Vertex, HashSet<Edge>> edge_map = new HashMap<>();
        HashMap<Vector3d, Vertex> vertices_on_edges = new HashMap<>();

        mesh = (T) mesh.copy();
        for (Object e : mesh.getEdges()) {
            Edge edge = (Edge) e;
            if(!vertices_on_edges.containsKey(edge)){
                Vertex vertex = mesh.new_vertex(edge.center().getX(), edge.center().getY(), edge.center().getZ());
                vertices_on_edges.put(edge.center(), vertex);
            }
        }

        for (Object face : mesh.getFaces()) {

            Face f = (Face) face;
            if (f.getVertices().length != 3) {
                throw new RuntimeException("Only triangles accepted");
            }


            Object[] edges = new Object[3];
            Vector2d circumCenter = Tools.circumCenter(f.getVertex(0), f.getVertex(1), f.getVertex(2));
            Vertex circum = mesh.new_vertex(circumCenter.getX(), circumCenter.getY(), 0);

            if(vertices_on_edges.containsKey(circum.getPosition())){
                circum = vertices_on_edges.get(circum.getPosition());
            }


            for (int i = 0; i < 3; i++) {
                ArrayList<Edge> edgesForVertex = new ArrayList<>();
                Edge left = f.getBoundaries()[(i + 2) % 3];
                Edge right = f.getBoundaries()[(i + 3) % 3];

                if (!circum.getPosition().equals(left.center())){
                    edgesForVertex.add(mesh.new_edge(
                            circum,
                            vertices_on_edges.get(left.center())));
                }
                if(!circum.getPosition().equals(right.center())){
                    edgesForVertex.add(mesh.new_edge(
                            circum,
                            vertices_on_edges.get(right.center())));
                }

                if (left.getLinkedBoundary() == null) {
                    edgesForVertex.add(mesh.new_edge(
                            f.getVertex(i),
                            vertices_on_edges.get(left.center())));
                }
                if (right.getLinkedBoundary() == null) {
                    edgesForVertex.add(mesh.new_edge(
                            f.getVertex(i),
                            vertices_on_edges.get(right.center())));
                }
                edges[i] = edgesForVertex;
            }
            //linking
            for (int i = 0; i < 3; i++) {
                ArrayList<Edge> e1 = (ArrayList<Edge>) edges[i];
                ArrayList<Edge> e2 = (ArrayList<Edge>) edges[(i + 1) % 3];
                if(e1.size() == 2)
                    e1.get(1).link(e2.get(0));
            }
            //setting
            for (int i = 0; i < 3; i++) {
                if (!edge_map.containsKey(f.getVertex(i))) {
                    edge_map.put(f.getVertex(i), new HashSet<>());
                }
                edge_map.get(f.getVertex(i)).addAll((ArrayList<Edge>) edges[i]);
            }
        }


        T m = (T) mesh.new_mesh();
        for (Vertex v : edge_map.keySet()) {

            System.out.println(edge_map.get(v).size());

            Edge[] e = generate_ordered_edges(mesh, edge_map.get(v));

            for(Edge k:e){
                System.out.println(System.identityHashCode(k.getV1()) + "  " + System.identityHashCode(k.getV2()));
            }
            System.out.println();

            Face f =mesh.new_face(e);
            m.addFace(f);
            f.bindData(new double[]{v.getPosition().getX(), v.getPosition().getY(), v.getPosition().getZ()});
        }
        return m;
    }

    public static <T extends Edge> T[] generate_ordered_edges(Mesh<?, T, ?, ?> mesh, HashSet<T> edges) {
        ArrayList<T> res = new ArrayList<>();
        res.addAll(edges);
        for (int i = 1; i < res.size(); i++) {
            Edge prev = res.get(i - 1);
            for (int n = i; n < res.size(); n++) {
                if (res.get(n).getV1() == prev.getV2()) {
                    Collections.swap(res, i, n);
                    break;
                }
                if (res.get(n).getV2() == prev.getV2()) {
                    res.get(n).swapVertices();
                    Collections.swap(res, i, n);
                    break;
                }
                if (n == res.size() - 1) {
                    System.err.println("Error");
                }
            }
        }

        return res.toArray(mesh.empty_edge_array());
    }

    public static void main(String[] args) {
        DefaultMesh mesh = (DefaultMesh) Generator.rectangle_mesh(new DefaultMesh(), 1,1,7,7);
        Transformer.generateNodeCenteredMesh(mesh);
//        System.out.println("...");

    }
}
