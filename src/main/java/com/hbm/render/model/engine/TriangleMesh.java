package com.hbm.render.model.engine;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.obj.ObjLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TriangleMesh {
    private final List<Float> vertices;
    private final List<Float> uvs;
    private final List<Float> normals;

    public TriangleMesh(List<Float> vertices, List<Float> uvs, List<Float> normals){
        this.vertices = vertices;
        this.uvs = uvs;
        this.normals = normals;
    }

    public static TriangleMesh loadFromObj(ResourceLocation location) throws IOException {
        List<Float> vertices = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        InputStream is = Minecraft.getInstance().getResourceManager().getResource(location).get().open();
        var reader = new BufferedReader(new InputStreamReader(is));

        List<Vector3f> tempV = new ArrayList<>();
        List<Vector2f> tempVT = new ArrayList<>();
        List<Vector3f> tempVN = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts.length == 0) continue;

            switch (parts[0]) {
                case "v" -> tempV.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                case "vt" -> tempVT.add(new Vector2f(Float.parseFloat(parts[1]), 1.0f - Float.parseFloat(parts[2]))); // UV翻转
                case "vn" -> tempVN.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                case "f" -> {
                    // 处理 f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
                    for (int i = 1; i <= 3; i++) {
                        String[] indices = parts[i].split("/");
                        Vector3f v = tempV.get(Integer.parseInt(indices[0]) - 1);
                        vertices.add(v.x()); vertices.add(v.y()); vertices.add(v.z());

                        if (indices.length > 1 && !indices[1].isEmpty()) {
                            Vector2f vt = tempVT.get(Integer.parseInt(indices[1]) - 1);
                            uvs.add(vt.x()); uvs.add(vt.y());
                        }
                        if (indices.length > 2) {
                            Vector3f vn = tempVN.get(Integer.parseInt(indices[2]) - 1);
                            normals.add(vn.x()); normals.add(vn.y()); normals.add(vn.z());
                        }
                    }
                }
            }
        }
        return new TriangleMesh(vertices, uvs, normals);
    }
}
