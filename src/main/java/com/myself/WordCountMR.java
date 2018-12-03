package com.myself;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Word Count MarReduce Application
 *
 */
public class WordCountMR
{

    public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer st = new StringTokenizer(value.toString());
            while (st.hasMoreTokens()) {
                word.set(st.nextToken());
                context.write(word, one)
            }
        }
    }

    //2. Reducer 类
    public static class WorkCountReducer extends Reducer<KEYIN, VALUEIN, KEYOUT, VALUEPUT>;

    //3. Job 任务
    public int run(String[] strings) throws Exception;

}
