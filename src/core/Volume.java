package core;

import core.vector.Vector3d;
import interfaces.Cell;
import structs.Mesh;
import tools.Tools;
import tools.Loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Volume extends Cell<Face, Volume, Volume> {

    public Volume(Face... faces){
        super();

        HashMap<Vertex, Integer> count = Tools.count(faces);
        for(Vertex v:count.keySet()){
            if(count.get(v) < 3){
                throw new RuntimeException("less than 3 vertices: " + v + "  have been found!");
            }
        }

        ArrayList<Vertex> vertices = new ArrayList<>(count.keySet());
        this.setVertices(vertices.toArray(new Vertex[0]));

        Face[] ar = new Face[faces.length];

        for(int i = 0; i < faces.length; i++){
            if(faces[i].getCell() != null){
                ar[i] = new Face(faces[i]);
            }else{
                ar[i] = faces[i];
            }
            ar[i].setCell(this);
        }

        this.setBoundaries(faces);
    }

    public double integral() {
        Vector3d center = center();
        double vol = 0;
        for(Face f:this.getBoundaries()){
            vol += f.integral() * f.orthogonalDistance(center) * 1/3d;
        }
        return vol;
    }

    @Override
    public double orthogonalDistance(Vector3d vec) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean inside(Vector3d vec) {
        throw new RuntimeException("Not yet implemented");
    }

    public Vector3d center(){
        return Tools.center(this.getVertices());
    }



    public static void main(String[] args) throws IOException {
        Vertex v1 = new Vertex(0,0,0);
        Vertex v2 = new Vertex(1,0,0);
        Vertex v3 = new Vertex(1,1,0);
        Vertex v4 = new Vertex(0,1,0);
        Edge e1 = new Edge(v1,v2);
        Edge e2 = new Edge(v2,v3);
        Edge e3 = new Edge(v3,v4);
        Edge e4 = new Edge(v4,v1);
        Face f1 = new Face(e1,e2,e3,e4);

        Vertex v5 = new Vertex(0,0,1);
        Vertex v6 = new Vertex(1,0,1);
        Vertex v7 = new Vertex(1,1,1);
        Vertex v8 = new Vertex(0,1,1);
        Edge e5 = new Edge(v5,v6);
        Edge e6 = new Edge(v6,v7);
        Edge e7 = new Edge(v7,v8);
        Edge e8 = new Edge(v8,v5);
        Face f2 = new Face(e5,e6,e7,e8);

        Edge e9 = new Edge(v1,v5);
        Edge e10 = new Edge(v2,v6);
        Edge e11 = new Edge(v3,v7);
        Edge e12 = new Edge(v4,v8);

        Face f3 = new Face(e1,e9,e10,e5);
        Face f4 = new Face(e2,e10,e11,e6);
        Face f5 = new Face(e3,e11,e12,e7);
        Face f6 = new Face(e4,e12,e9,e8);

        Volume volume_1 = new Volume(f1,f2,f3,f4,f5,f6) {

            @Override
            public double orthogonalDistance(Vector3d vec) {
                return 0;
            }

            @Override
            public boolean inside(Vector3d vec) {
                return false;
            }
        };

        ArrayList<Volume> volumes = new ArrayList<>();
        volumes.add(volume_1);

        Mesh mesh = new Mesh(volumes) {
            @Override
            public Vertex new_vertex(double x, double y, double z) {
                return null;
            }

            @Override
            public Edge new_edge(Vertex v1, Vertex v2) {
                return null;
            }

            @Override
            public Face new_face(Edge[] edges) {
                return null;
            }

            @Override
            public Volume new_volume(Face... faces) {
                return null;
            }
        };

        Loader.write("tes.mesh", mesh);

        Loader.load("tes.mesh", mesh);
        Loader.write("out.mesh", mesh);

    }

    @Override
    public void prepare_data() {

    }

    @Override
    public void process_data() {

    }
}
