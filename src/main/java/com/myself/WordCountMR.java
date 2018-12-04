package com.myself;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Word Count MarReduce Application
 *
 */
public class WordCountMR
{

    /**
     * @KEYIN 每一行的起始位置（偏移量offset）
     * @KEYIN 每一行的文本内容
     * @KEYOUT 单个单词
     * @VALUEOUT 单词的次数（固定为1）
     */

    public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer st = new StringTokenizer(value.toString());
            while (st.hasMoreTokens()) {
                word.set(st.nextToken());
                context.write(word, one);
            }
        }
    }

    /**
     * @KEYIN 单个单词
     * @KEYIN 单词的次数（固定为1）
     * @KEYOUT 单个单词
     * @VALUEOUT 单词出现的次数之和
     */
    public static class WordCountReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }

            result.set(sum);
            context.write(key, result);
        }
    }


//    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.printf("Usage: %s [generic options] <input> <output> \n",
                    getClass().getSimpleName());

            return -1;
        }

        //设置HADOOP_HOME目录(可替代设置环境变量)
        System.setProperty("hadoop.home.dir", "/Users/shijiansheng/hadoop-2.7.3");


        //设置访问用户
        System.setProperty("HADOOP_USER_NAME", "root");

        Configuration conf = new Configuration();
//        Configuration conf = getConf();
        //datanode使用客户端的hosts
        conf.set("dfs.client.use.datanode.hostname", "true");
        //开启跨平台提交
        conf.set("mapreduce.app-submission.cross-platform", "true");

        Job job = Job.getInstance(conf, "WordCountMapReduceApp");
        job.setJarByClass(getClass());
        job.setJar("/Users/shijiansheng/workspace/WordCoundMR/target/WordCountMR.jar");

        // 设置输入和输出路径
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //设置自定义Mapper和Reducer类
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        // 指定reduce输出的<K,V>类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //提交作业
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main( String[] args ) throws Exception {
        //仅供测试
//        if (args == null || args.length == 0) {
//            args = new String[]{
//                    "hdfs://ha-cluster/user/root/input/",
//                    "hdfs://ha-cluster/user/root/output/"
//            };
//        }

        WordCountMR wordCountApp = new WordCountMR();
//
        int exitCode = wordCountApp.run(args);

//        int exitCode = ToolRunner.run(new WordCountMR(), args);

        System.exit(exitCode);
    }

}
