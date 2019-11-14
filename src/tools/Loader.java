package tools;

import core.Edge;
import core.Face;
import core.Vertex;
import core.Volume;
import core.vector.Vector3d;
import interfaces.Boundary;
import interfaces.Cell;
import interfaces.Container;
import structs.Mesh;
import structs.Triangle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Loader {

    private static double[] bufferData(String[] ar, int beginIndex) {
        if (ar.length - beginIndex == 0) return null;
        double[] out = new double[ar.length - beginIndex];
        for (int i = beginIndex; i < ar.length; i++) {
            out[i-beginIndex] = Double.parseDouble(ar[i]);
        }
        return out;
    }

    public static void load(String f, Mesh mesh) throws IOException {
        File file = new File(f);

        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        ArrayList<Face> faces = new ArrayList<>();
        ArrayList<Volume> volumes = new ArrayList<>();

        String st;
        while ((st = br.readLine()) != null) {
            st = st.replace("  ", " ");
            String[] split = st.split(" ");
            if (split[0].equals("v")) {
                Vertex v = mesh.new_vertex(
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3]));
                v.bindData(bufferData(split, 4));
                vertices.add(v);
            } else if (split[0].equals("e")) {
                int id1 = Integer.parseInt(split[1]);
                int id2 = Integer.parseInt(split[2]);
                Edge e = mesh.new_edge(vertices.get(id1), vertices.get(id2));
                e.bindData(bufferData(split, 3));
                edges.add(e);
            } else if (split[0].equals("f")) {
                if (split[1].startsWith("s")) {
                    int count = Integer.parseInt(split[1].substring(1));
                    Edge[] eL = new Edge[count];
                    for (int i = 0; i < count; i++) {
                        eL[i] = edges.get(Integer.parseInt(split[2 + i]));
                    }
                    Face t = mesh.new_face(eL);
                    t.bindData(bufferData(split, 2 + count));
                    faces.add(t);
                }
            } else if (split[0].equals("k")) {
                if (split[1].startsWith("s")) {
                    int fac = Integer.parseInt(split[1].substring(1));
                    Face[] fAr = new Face[fac];

                    for (int i = 2; i < fac + 2; i++) {
                        fAr[i - 2] = faces.get(Integer.parseInt(split[i]));
                    }
                    Volume volume = mesh.new_volume(fAr);
                    volume.bindData(bufferData(split, 2 + fac));
                    volumes.add(volume);
                }
            } else if (split[0].startsWith("b")) {
                Integer e1 = Integer.parseInt(split[1]);
                Integer e2 = Integer.parseInt(split[2]);
                if (split[0].endsWith("e")) {
                    edges.get(e1).link(edges.get(e2));
                } else {
                    faces.get(e1).link(faces.get(e2));
                }
            }
        }

        mesh.setEdges(edges);
        mesh.setVertices(vertices);
        mesh.setVolumes(volumes);
        mesh.setFaces(faces);
        mesh.process_data();
        //return mesh;
    }

    private static String writeData(Container c) {
        StringBuilder builder = new StringBuilder();
        double[] data = c.getData();
        if (data == null || data.length == 0) return "";
        for (int i = 0; i < data.length - 1; i++) {
            builder.append(data[i] + " ");
        }
        builder.append(data[data.length - 1]);
        return builder.toString();
    }

    public static void write(String f, Mesh mesh) throws IOException {
        File file = new File(f);
        mesh.prepare_data();

        BufferedWriter br = new BufferedWriter(new FileWriter(file));

        ArrayList<Vertex> vertices = mesh.getVertices();
        ArrayList<Edge> edges = mesh.getEdges();
        ArrayList<Face> faces = mesh.getFaces();
        ArrayList<Volume> volumes = mesh.getVolumes();

        HashMap<Vertex, Integer> vertexHashMap = new HashMap<>();
        HashMap<Edge, Integer> edgeHashMap = new HashMap<>();
        HashMap<Face, Integer> faceHashMap = new HashMap<>();


        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            vertexHashMap.put(v, i);
            br.write("v " +
                    v.getX() + " " +
                    v.getY() + " " +
                    v.getZ() + " " +
                    writeData(v) + "\n");
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge v = edges.get(i);
            edgeHashMap.put(v, i);
            br.write("e " +
                    vertexHashMap.get(v.getV1()) + " " +
                    vertexHashMap.get(v.getV2()) + " " +
                    writeData(v) + "\n");
        }

        for (int i = 0; i < faces.size(); i++) {
            Face v = faces.get(i);
            faceHashMap.put(v, i);


            br.write("f s" + v.getBoundaries().length);
            for (Boundary e : v.getBoundaries()) {
                Edge edg = (Edge) e;
                br.write(" " + edgeHashMap.get(edg));
            }
            br.write(" " + writeData(v) + "\n");

        }

        for (Volume v : volumes) {

            br.write("k s" + v.getBoundaries().length);
            for (Boundary e : v.getBoundaries()) {
                Face edg = (Face) e;
                br.write(" " + faceHashMap.get(edg));
            }
            br.write(" " + writeData(v) + "\n");

        }

        HashSet<Edge> hashedEdges = new HashSet<>();
        for (Edge e : edges) {
            ArrayList<Edge> linked = e.getLinked_boundaries();
            if (!hashedEdges.contains(linked.get(0))) {
                hashedEdges.addAll(linked);
                for (int i = 1; i < linked.size(); i++) {
                    br.write("be " +
                            edgeHashMap.get(linked.get(0)) + " " +
                            edgeHashMap.get(linked.get(i)) + "\n");
                }
            }
        }

        HashSet<Face> hashedFaces = new HashSet<>();
        for (Face fc : faces) {
            ArrayList<Face> linked = fc.getLinked_boundaries();
            if (!hashedFaces.contains(linked.get(0))) {
                for (int i = 1; i < linked.size(); i++) {
                    br.write("bf " +
                            faceHashMap.get(linked.get(0)) + " " +
                            faceHashMap.get(linked.get(i)) + "\n");
                }
            }
        }

        br.close();

    }

}


