
package io.maddsoft.hbadgerstation.stl;

import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Based on <a href="https://github.com/alejandrocarcamo/STLLoader/tree/master">https://github.com/alejandrocarcamo/STLLoader/tree/master</a>
 * Modifications copyright (C) 2025 Daniel Madejek
 */
@Slf4j
public class StlLoader {


    private HashMap<String, Point3D> uniquePoints;
    private HashMap<Point3D, Integer> pointsDictionary;

    public TriangleMesh loadStl (String path) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine();
            String tmp = reader.readLine();
            if (null != tmp && tmp.contains("facet")) {
                return loadAsciiStl(path);
            }
            else {
                return loadBinaryStl(path);
            }
        } finally {
            clear();
        }
    }

    public  TriangleMesh loadBinaryStl(String path) throws IOException {
        TriangleMesh result;
        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(Paths.get(path), EnumSet.of(StandardOpenOption.READ))){
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(80);
            int triangulates =  buffer.getInt();
            uniquePoints = HashMap.newHashMap(triangulates*3);
            pointsDictionary = new HashMap<>();
            result = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
            result.getTexCoords().addAll(0f,0f);

            for(int i = 0; i < triangulates; i++) {
                Point3D normal = new Point3D( buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
                result.getNormals().addAll((float)normal.getX(),(float)normal.getY(), (float) normal.getZ());

                Point3D p1 = getPoint3D(result, buffer);
                Point3D p2 = getPoint3D(result, buffer);
                Point3D p3 = getPoint3D(result, buffer);
                int firstIndex = result.getNormals().size() / 3-1;
                result.getFaces().addAll(
                    pointsDictionary.get(p1),
                    firstIndex,
                    0,
                    pointsDictionary.get(p2),
                    firstIndex,
                    0,
                    pointsDictionary.get(p3),
                    firstIndex,
                    0
                );
                buffer.position(buffer.position()+2);
            }
            buffer.clear();
        }
        return result;
    }

    private Point3D getPoint3D(TriangleMesh result, MappedByteBuffer buffer) {
        Point3D p1;
        p1 = new Point3D(buffer.getFloat(),buffer.getFloat(), buffer.getFloat());
        if (null == uniquePoints.putIfAbsent(p1.toString(), p1)) {
            pointsDictionary.put(p1, uniquePoints.size()-1);
            result.getPoints().addAll((float) p1.getX(),(float) p1.getY(),(float) p1.getZ());
        }
        return p1;
    }

    public  TriangleMesh loadAsciiStl(String path) throws IOException
    {
        TriangleMesh result;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String tmp = reader.readLine();
            if (!tmp.startsWith("solid")) {
                throw new IOException("Invalid ASCII STL ("+path+")");
            }
            uniquePoints = new HashMap<>();
            pointsDictionary = new HashMap<>();
            result = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
            result.getTexCoords().addAll(0f,0f);

            while (null != (tmp = reader.readLine())) {
                if (tmp.contains("facet")) {
                    String[] split = tmp.replaceFirst("facet normal", "").trim().split(" ");
                    Point3D normal = new Point3D(
                            Float.parseFloat(split[0]),
                            Float.parseFloat(split[1]),
                            Float.parseFloat(split[2]));
                    result.getNormals().addAll((float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
                    reader.readLine();

                    Point3D p1 = getPoint3D(reader);
                    Point3D p2 = getPoint3D(reader);
                    Point3D p3 = getPoint3D(reader);

                    if (null == uniquePoints.putIfAbsent(p1.toString(), p1)) {
                        pointsDictionary.put(p1, uniquePoints.size() - 1);
                        result.getPoints().addAll((float) p1.getX(), (float) p1.getY(), (float) p1.getZ());
                    }
                    if (null == uniquePoints.putIfAbsent(p2.toString(), p2)) {
                        pointsDictionary.put(p2, uniquePoints.size() - 1);
                        result.getPoints().addAll((float) p2.getX(), (float) p2.getY(), (float) p2.getZ());
                    }
                    if (null == uniquePoints.putIfAbsent(p3.toString(), p3)) {
                        pointsDictionary.put(p3, uniquePoints.size() - 1);
                        result.getPoints().addAll((float) p3.getX(), (float) p3.getY(), (float) p3.getZ());
                    }
                    int firstIndex = result.getNormals().size() / 3 - 1;
                    result.getFaces().addAll(
                            pointsDictionary.get(p1),
                            firstIndex,
                            0,
                            pointsDictionary.get(p2),
                            firstIndex,
                            0,
                            pointsDictionary.get(p3),
                            firstIndex,
                            0
                    );
                    reader.readLine(); //endloop
                    reader.readLine(); //endfacet
                }
            }


        }
        return result;
    }

    private Point3D getPoint3D(BufferedReader reader) throws IOException {
        String tmp;
        String[] split;
        float coordinateX;
        float coordinateY;
        float coordinateZ;
        Point3D p1;
        tmp = reader.readLine();
        split = tmp.replaceFirst("vertex", "").trim().split(" ");
        coordinateX = (Float.parseFloat(split[0]));
        coordinateY = (Float.parseFloat(split[1]));
        coordinateZ = (Float.parseFloat(split[2]));
        p1 = new Point3D(coordinateX, coordinateY, coordinateZ);
        return p1;
    }

    private void clear() {
        if (uniquePoints != null) {
            uniquePoints.clear();
            uniquePoints = null;
        }
        if (pointsDictionary != null) {
            pointsDictionary.clear();
            pointsDictionary = null;
        }
        System.gc();
    }
}
