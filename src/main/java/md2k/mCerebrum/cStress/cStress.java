package md2k.mCerebrum.cStress;

import md2k.mCerebrum.cStress.Autosense.AUTOSENSE;
import md2k.mCerebrum.cStress.Features.AccelerometerFeatures;
import md2k.mCerebrum.cStress.Features.ECGFeatures;
import md2k.mCerebrum.cStress.Features.RIPFeatures;
import md2k.mCerebrum.cStress.Library.DataStream;
import md2k.mCerebrum.cStress.Library.DataStreams;
import md2k.mCerebrum.cStress.Library.FeatureVector;
import md2k.mCerebrum.cStress.Structs.DataPoint;

import libsvm.*;
import md2k.mCerebrum.cStress.Structs.StressProbability;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Date;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Timothy Hnat <twhnat@memphis.edu>
 * - Karen Hovsepian <karoaper@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class cStress {

    long windowStartTime = -1;

    long windowSize;
    private String participant;

    private DataStreams datastreams = new DataStreams();


    public cStress(long windowSize, String svmModelFile, String featureVectorParameterFile, String participant) {
        this.windowSize = windowSize;
        this.participant = participant;

        //Configure Data Streams
        datastreams.get("org.md2k.cstress.data.ecg").metadata.put("frequency", 64.0);
        datastreams.get("org.md2k.cstress.data.ecg").metadata.put("channelID", AUTOSENSE.CHEST_ECG);

        datastreams.get("org.md2k.cstress.data.rip").metadata.put("frequency", 64.0 / 3.0);
        datastreams.get("org.md2k.cstress.data.rip").metadata.put("channelID", AUTOSENSE.CHEST_RIP);

        datastreams.get("org.md2k.cstress.data.accelx").metadata.put("frequency", 64.0 / 6.0);
        datastreams.get("org.md2k.cstress.data.accelx").metadata.put("channelID", AUTOSENSE.CHEST_ACCEL_X);

        datastreams.get("org.md2k.cstress.data.accely").metadata.put("frequency", 64.0 / 6.0);
        datastreams.get("org.md2k.cstress.data.accely").metadata.put("channelID", AUTOSENSE.CHEST_ACCEL_X);

        datastreams.get("org.md2k.cstress.data.accelz").metadata.put("frequency", 64.0 / 6.0);
        datastreams.get("org.md2k.cstress.data.accelz").metadata.put("channelID", AUTOSENSE.CHEST_ACCEL_X);

        resetDataStreams();


//        try {
//            Model = svm.svm_load_model(svmModelFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        this.featureVectorMean = new double[37];
//        this.featureVectorStd = new double[37];
//        loadFVParameters(featureVectorParameterFile);

    }

//    private void loadFVParameters(String featureVectorParameterFile) {
//
//        BufferedReader br = null;
//        String line = "";
//        String csvSplitBy = ",";
//        int count;
//        try {
//            br = new BufferedReader(new FileReader(featureVectorParameterFile));
//            count = 0;
//            while ((line = br.readLine()) != null) {
//                String[] meanstd = line.split(csvSplitBy);
//                this.featureVectorMean[count] = Double.parseDouble(meanstd[0]);
//                this.featureVectorStd[count] = Double.parseDouble(meanstd[1]);
//                count++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


//    private StressProbability evaluteStressModel(AccelerometerFeatures accelFeatures, ECGFeatures ecgFeatures, RIPFeatures ripFeatures, double bias) {
//
//        StressProbability stressResult = new StressProbability(-1, 0.0);
//


//
//        featureVector = normalizeFV(featureVector);
//
//        boolean invalid = false;
//        for(double d: featureVector) {
//            if (Double.isInfinite(d) || Double.isNaN(d)) {
//                invalid = true;
//            }
//        }
//
//        if (!activityCheck(accelFeatures) && !invalid) {
//            //SVM evaluation
//            svm_node[] data = new svm_node[featureVector.length];
//            for (int i = 0; i < featureVector.length; i++) {
//                data[i] = new svm_node();
//                data[i].index = i;
//                data[i].value = featureVector[i];
//            }
//
//            stressResult.probability = svm.svm_predict(Model, data);
//            if (stressResult.probability < bias) {
//                stressResult.label = AUTOSENSE.NOT_STRESSED;
//            } else {
//                stressResult.label = AUTOSENSE.STRESSED;
//            }
//        }
//
//        //Basic Features
//        DataPoint[] af = accelFeatures.rawFeatures();
//        DataPoint[] rr_intervals = ecgFeatures.rawFeatures();
//        DataPoint[] peaks = ripFeatures.rawPeakFeatures();
//        DataPoint[] valleys = ripFeatures.rawValleyFeatures();
//
//        return stressResult;
//    }
//
//    private double[] normalizeFV(double[] featureVector) {
//        double[] result = new double[featureVector.length];
//
//        for (int i = 0; i < featureVector.length; i++) {
//            result[i] = (featureVector[i] - this.featureVectorMean[i]) / this.featureVectorStd[i];
//        }
//        return result;
//    }
//
//    private boolean activityCheck(AccelerometerFeatures accelFeatures) {
//        return accelFeatures.Activity;
//    }


    public StressProbability process() {

        StressProbability probabilityOfStress = null;

        //TODO: This should be moved outside of the stress applications to intercept data before arriving here
//            //This check must happen before any normalization.  It operates on the RAW signals.
//            RipQualityCalculation ripQuality = new RipQualityCalculation(5, 50, 4500, 20, 2, 20, 150);
//            ECGQualityCalculation ecgQuality = new ECGQualityCalculation(3, 50, 4500, 20, 2, 47);
//
//            if (!ripQuality.computeQuality(rip, 5 * 1000, 0.67) || !ecgQuality.computeQuality(ecg, 5 * 1000, 0.67)) { //Check for 67% of the data to be of Quality within 5 second windows.
//                return probabilityOfStress; //data quality failure
//            }
//
//

        AccelerometerFeatures af = new AccelerometerFeatures(datastreams, AUTOSENSE.ACTIVITY_THRESHOLD, AUTOSENSE.ACCEL_WINDOW_SIZE);
        ECGFeatures ef = new ECGFeatures(datastreams);
        RIPFeatures rf = new RIPFeatures(datastreams);

        FeatureVector fv = computeStressFeatures(datastreams);

        if (fv != null) {
            fv.persist("/Users/hnat/Downloads/processedrawdata/" + participant + "/org.md2k.cstress.fv.csv");
        }

//                probabilityOfStress = evaluteStressModel(accelFeatures, ecgFeatures, ripFeatures, AUTOSENSE.STRESS_PROBABILTY_THRESHOLD);

        return probabilityOfStress;
    }

    private FeatureVector computeStressFeatures(DataStreams datastreams) {

        try {
        /* List of features for SVM model

         ECG - RR interval variance
         ECG - RR interval quartile deviation
         ECG - RR interval low frequency energy

         ECG - RR interval medium frequency energy
         *ECG - RR interval high frequency energy
         *ECG - RR interval low-high frequency energy ratio
         *ECG - RR interval mean
         ECG - RR interval median
         *ECG - RR interval 80th percentile
         ECG - RR interval 20th percentile
         ECG - RR interval heart-rate
         */

            DescriptiveStatistics RRint = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.ecg.rr_value").getNormalizedValues());
            double ECG_RR_Interval_Variance = RRint.getVariance();
            double ECG_RR_Interval_Quartile_Deviation = (RRint.getPercentile(75) - RRint.getPercentile(25)) / 2.0;

            DataStream lombLE = datastreams.get("org.md2k.cstress.data.ecg.rr.LombLowFrequencyEnergy");
            double ECG_RR_Interval_Low_Frequency_Energy = (lombLE.data.get(0).value-lombLE.stats.getMean()) / lombLE.stats.getStandardDeviation();
            
            DataStream lombME = datastreams.get("org.md2k.cstress.data.ecg.rr.LombMediumFrequencyEnergy");
            double ECG_RR_Interval_Medium_Frequency_Energy = (lombME.data.get(0).value-lombME.stats.getMean()) / lombME.stats.getStandardDeviation();

            DataStream lombHE = datastreams.get("org.md2k.cstress.data.ecg.rr.LombHighFrequencyEnergy");
            double ECG_RR_Interval_High_Frequency_Energy = (lombHE.data.get(0).value-lombHE.stats.getMean()) / lombHE.stats.getStandardDeviation();

            DataStream lombLH = datastreams.get("org.md2k.cstress.data.ecg.rr.LowHighFrequencyEnergyRatio");
            double ECG_RR_Interval_Low_High_Frequency_Energy_Ratio = (lombLH.data.get(0).value-lombLH.stats.getMean()) / lombLH.stats.getStandardDeviation();

            double ECG_RR_Interval_Mean = RRint.getMean();
            double ECG_RR_Interval_Median = RRint.getPercentile(50);
            double ECG_RR_Interval_80thPercentile = RRint.getPercentile(80);
            double ECG_RR_Interval_20thPercentile = RRint.getPercentile(20);

            DescriptiveStatistics heartrate = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.ecg.rr.heartrate").getNormalizedValues());
            double ECG_RR_Interval_Heart_Rate = heartrate.getMean();

         /*
         RIP - Inspiration Duration - quartile deviation
         RIP - Inspiration Duration - mean
         RIP - Inspiration Duration - median
         RIP - Inspiration Duration - 80th percentile
         */

            DescriptiveStatistics InspDuration = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.rip.inspduration").getNormalizedValues());

            double RIP_Inspiration_Duration_Quartile_Deviation = (InspDuration.getPercentile(75) - InspDuration.getPercentile(25)) / 2.0;
            double RIP_Inspiration_Duration_Mean = InspDuration.getMean();
            double RIP_Inspiration_Duration_Median = InspDuration.getPercentile(50);
            double RIP_Inspiration_Duration_80thPercentile = InspDuration.getPercentile(80);

         /*
         RIP - Expiration Duration - quartile deviation
         RIP - Expiration Duration - mean
         RIP - Expiration Duration - median
         RIP - Expiration Duration - 80th percentile
         */

            DescriptiveStatistics ExprDuration = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.rip.exprduration").getNormalizedValues());

            double RIP_Expiration_Duration_Quartile_Deviation = (ExprDuration.getPercentile(75) - ExprDuration.getPercentile(25)) / 2.0;
            double RIP_Expiration_Duration_Mean = ExprDuration.getMean();
            double RIP_Expiration_Duration_Median = ExprDuration.getPercentile(50);
            double RIP_Expiration_Duration_80thPercentile = ExprDuration.getPercentile(80);
         /*
         RIP - Respiration Duration - quartile deviation
         RIP - Respiration Duration - mean
         RIP - Respiration Duration - median
         RIP - Respiration Duration - 80th percentile
         */

            DescriptiveStatistics RespDuration = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.rip.respduration").getNormalizedValues());

            double RIP_Respiration_Duration_Quartile_Deviation = (RespDuration.getPercentile(75) - RespDuration.getPercentile(25)) / 2.0;
            double RIP_Respiration_Duration_Mean = RespDuration.getMean();
            double RIP_Respiration_Duration_Median = RespDuration.getPercentile(50);
            double RIP_Respiration_Duration_80thPercentile = RespDuration.getPercentile(80);
        
         /*
         RIP - Inspiration-Expiration Duration Ratio - quartile deviation
         *RIP - Inspiration-Expiration Duration Ratio - mean
         RIP - Inspiration-Expiration Duration Ratio - median
         RIP - Inspiration-Expiration Duration Ratio - 80th percentile
         */
            DescriptiveStatistics InspExprDuration = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.rip.IERatio").getNormalizedValues());

            double RIP_Inspiration_Expiration_Duration_Quartile_Deviation = (InspExprDuration.getPercentile(75) - InspExprDuration.getPercentile(25)) / 2.0;
            double RIP_Inspiration_Expiration_Duration_Mean = InspExprDuration.getMean();
            double RIP_Inspiration_Expiration_Duration_Median = InspExprDuration.getPercentile(50);
            double RIP_Inspiration_Expiration_Duration_80thPercentile = InspExprDuration.getPercentile(80);

         /*
         RIP - Stretch - quartile deviation
         RIP - Stretch - mean
         *RIP - Stretch - median
         RIP - Stretch - 80th percentile
         */
            DescriptiveStatistics Stretch = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.rip.stretch").getNormalizedValues());

            double RIP_Stretch_Quartile_Deviation = (Stretch.getPercentile(75) - Stretch.getPercentile(25)) / 2.0;
            double RIP_Stretch_Mean = Stretch.getMean();
            double RIP_Stretch_Median = Stretch.getPercentile(50);
            double RIP_Stretch_80thPercentile = Stretch.getPercentile(80);
         /*
         *RIP - Breath-rate
         */
            DataStream breathRate = datastreams.get("org.md2k.cstress.data.rip.BreathRate");
            double RIP_Breath_Rate = (breathRate.data.get(0).value - breathRate.stats.getMean()) / breathRate.stats.getStandardDeviation();

         /*
         *RIP - Inspiration Minute Volume
         */
            DataStream minVent = datastreams.get("org.md2k.cstress.data.rip.MinuteVentilation");
            double RIP_Inspiration_Minute_Ventilation = (minVent.data.get(0).value - minVent.stats.getMean()) / minVent.stats.getStandardDeviation();

         /*
         RIP+ECG - Respiratory Sinus Arrhythmia (RSA) - quartile deviation
         RIP+ECG - Respiratory Sinus Arrhythmia (RSA) - mean
         RIP+ECG - Respiratory Sinus Arrhythmia (RSA) - median
         RIP+ECG - Respiratory Sinus Arrhythmia (RSA) - 80th percentile
         */

            DescriptiveStatistics RSA = new DescriptiveStatistics(datastreams.get("org.md2k.cstress.data.rip.RSA").getNormalizedValues());

            double RSA_Quartile_Deviation = (RSA.getPercentile(75) - RSA.getPercentile(25)) / 2.0;
            double RSA_Mean = RSA.getMean();
            double RSA_Median = RSA.getPercentile(50);
            double RSA_80thPercentile = RSA.getPercentile(80);

            double[] featureVector = {
                    ECG_RR_Interval_Variance,                               // 1
                    ECG_RR_Interval_Low_High_Frequency_Energy_Ratio,        // 2
                    ECG_RR_Interval_High_Frequency_Energy,                  // 3
                    ECG_RR_Interval_Medium_Frequency_Energy,                // 4
                    ECG_RR_Interval_Low_Frequency_Energy,                   // 5
                    ECG_RR_Interval_Mean,                                   // 6
                    ECG_RR_Interval_Median,                                 // 7
                    ECG_RR_Interval_Quartile_Deviation,                     // 8
                    ECG_RR_Interval_80thPercentile,                         // 9
                    ECG_RR_Interval_20thPercentile,                         // 10
                    ECG_RR_Interval_Heart_Rate,                             // 11

                    RIP_Breath_Rate,                                        // 12
                    RIP_Inspiration_Minute_Ventilation,                          // 13

                    RIP_Inspiration_Duration_Quartile_Deviation,            // 14
                    RIP_Inspiration_Duration_Mean,                          // 15
                    RIP_Inspiration_Duration_Median,                        // 16
                    RIP_Inspiration_Duration_80thPercentile,                // 17

                    RIP_Expiration_Duration_Quartile_Deviation,             // 18
                    RIP_Expiration_Duration_Mean,                           // 19
                    RIP_Expiration_Duration_Median,                         // 20
                    RIP_Expiration_Duration_80thPercentile,                 // 21

                    RIP_Respiration_Duration_Quartile_Deviation,            // 22
                    RIP_Respiration_Duration_Mean,                          // 23
                    RIP_Respiration_Duration_Median,                        // 24
                    RIP_Respiration_Duration_80thPercentile,                // 25

                    RIP_Inspiration_Expiration_Duration_Quartile_Deviation, // 26
                    RIP_Inspiration_Expiration_Duration_Mean,               // 27
                    RIP_Inspiration_Expiration_Duration_Median,             // 28
                    RIP_Inspiration_Expiration_Duration_80thPercentile,     // 29

                    RIP_Stretch_Quartile_Deviation,                         // 30
                    RIP_Stretch_Mean,                                       // 31
                    RIP_Stretch_Median,                                     // 32
                    RIP_Stretch_80thPercentile,                             // 33

                    RSA_Quartile_Deviation,                                 // 34
                    RSA_Mean,                                               // 35
                    RSA_Median,                                             // 36
                    RSA_80thPercentile                                      // 37
            };

            FeatureVector fv = new FeatureVector(windowStartTime, featureVector);
            return fv;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        return null;
    }


    public StressProbability add(int channel, DataPoint dp) {
        StressProbability result = null;

        if (this.windowStartTime < 0)
            this.windowStartTime = nextEpochTimestamp(dp.timestamp);

        if ((dp.timestamp - windowStartTime) >= this.windowSize) { //Process the buffer every windowSize milliseconds
            result = process();
            resetDataStreams();
            this.windowStartTime += AUTOSENSE.SAMPLE_LENGTH_SECS * 1000; //Add 60 seconds to the timestamp
        }

        if (dp.timestamp >= this.windowStartTime) {
            switch (channel) {
                case AUTOSENSE.CHEST_ECG:
                    datastreams.get("org.md2k.cstress.data.ecg").add(dp);
                    break;

                case AUTOSENSE.CHEST_RIP:
                    datastreams.get("org.md2k.cstress.data.rip").add(dp);
                    break;

                case AUTOSENSE.CHEST_ACCEL_X:
                    datastreams.get("org.md2k.cstress.data.accelx").add(dp);
                    break;

                case AUTOSENSE.CHEST_ACCEL_Y:
                    datastreams.get("org.md2k.cstress.data.accely").add(dp);
                    break;

                case AUTOSENSE.CHEST_ACCEL_Z:
                    datastreams.get("org.md2k.cstress.data.accelz").add(dp);
                    break;

                default:
                    System.out.println("NOT INTERESTED: " + dp);
                    break;
            }
        }

        return result;
    }

    private long nextEpochTimestamp(long timestamp) {
        long previousMinute = timestamp / (60 * 1000);
        Date date = new Date((previousMinute + 1) * (60 * 1000));
        return date.getTime();
    }

    private void resetDataStreams() {
        datastreams.persist("/Users/hnat/Downloads/processedrawdata/" + participant + "/");
        datastreams.reset();
    }


}
