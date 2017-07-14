package com.silkbank.consumerBanking.Business;

import com.silkbank.consumersBanking.commons.AppLogger;
import com.silkbank.consumersBanking.commons.Functions;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

import static com.silkbank.consumersBanking.commons.Constants.EVENT_STORE.*;
import static org.apache.spark.sql.functions.*;

/**
 * Created by ll186048 on 28/06/2017.
 * <p>
 * DPI_UDN to Event_Store transformation
 */
public class BusinessMain {

    /**
     * Execute the transformation.
     *
     * @param args array of arguments: [ "source category.source feed", "target category.target feed", "timestamp"]
     */
    public static void main(String[] args) {
    	        
    	Logger logger = AppLogger.getLoggerInstance("Transformation");
    	logger.info(">>-->> Testing Logger ...");
    	
    	if (args.length < 3) {
            System.out.println("Usage: spark-submit ... source_table target_table timestamp [incremental] [input_partition]");

            System.exit(1);
        }

        String source = args[0]; //"dd_jazz_stg_dpi.dpi_logs"
        String target = args[1]; //"dd_jazz_stg_dpi.event_store"
        String timestamp = args[2]; //new Date().toString(); //"1498893716970"
        String where = "";
        boolean incremental = false;

        if (args.length == 4) {
            incremental = Boolean.valueOf(args[3]);
        }

        if (args.length == 5) {
            where = String.format("where %s", args[4]);
        }

        SparkSession spark = SparkSession
        		.builder()
                .appName("DPI_UDN to Event_Store Transformation")
                .enableHiveSupport()
                .getOrCreate();

        spark.udf().register(LOCATION, Functions.location, DataTypes.StringType);

        Dataset<Row> sourceDf = spark.sql(String.format("select * from %s %s", source, where));

        Dataset<Row> transformedDf;
        transformedDf = sourceDf.select(
        		substring(from_unixtime(col("starttimesecond")), 0, 10).as(EVENT_DATE),
                lit("DPI").as(STREAM_TYPE),
                from_unixtime(col("starttimesecond")).as(EVENT_TIMESTAMP)
                  //processing_dttm is always added in the feed initialization
        );

        transformedDf.show();

        transformedDf.createOrReplaceTempView("DpiUdnToEventStore");
        
        if (incremental) {
            spark.sql(String.format("insert into table %s select * from DpiUdnToEventStore", target));
        } else {
            spark.sql(String.format("insert overwrite table %s select * from DpiUdnToEventStore", target));
        }
        
        //Very Important; Close Spark Resources ...
      	spark.close();
    }
}