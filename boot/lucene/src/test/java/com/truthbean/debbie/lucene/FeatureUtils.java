/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lucene;

import java.nio.ByteBuffer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-13 14:44
 */
public class FeatureUtils {
    /**
     * index中的二进制转化为double数组
     *
     * @param doubles feature value
     * @return value in index
     */
    public static byte[] doubleArray2bytes(double[] doubles) {
        int times = Double.BYTES;
        int dimension = doubles.length;
        byte[] bytes = new byte[dimension * times];
        for (int i = 0; i < doubles.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putDouble(doubles[i]);
        }
        return bytes;
    }
    /**
     * index中的二进制转化为double数组
     *
     * @param bytes feature value in index
     * @return double array
     */
    public static double[] bytes2doubleArray(byte[] bytes, int dimension) {
        //bytes = unGZip(bytes);
        double[] histogram = new double[dimension];
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[bytes.length / times];
        if (doubles.length != histogram.length) {
            return null;
        }
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(bytes, i * times, times).getDouble();
            histogram[i] = doubles[i];
        }
        return histogram;
    }

    /**
     * 计算两个特征数组的距离（相似度）
     *
     * @param featureSearch 待检索图片的特征数组
     * @param featureIndex  index中存储的特征数组
     * @return 相似度
     */
    public static double getDistance(double[] featureSearch, double[] featureIndex) {
        double result = cosineSimilarity(featureSearch, featureIndex);
        return result * 100;
    }

    /**
     * 两空间坐标求余弦值
     * @param vectorA 点A
     * @param vectorB 点B
     * @return 余弦值
     */
    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0d;
        double normA = 0.0d;
        double normB = 0.0d;
        if (vectorA == null) return 0.0D;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        double result = 0.0d;
        if (normB != 0 && normA != 0) {
            result = (0.5 + 0.5 * (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))));
        }
        return result;
    }
}
