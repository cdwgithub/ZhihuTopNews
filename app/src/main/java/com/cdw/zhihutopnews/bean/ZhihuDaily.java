package com.cdw.zhihutopnews.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CDW on 2016/11/3.
 */

public class ZhihuDaily {

    /* {"date":"20161107","stories":
         [{"date":"20161107","hasFadedIn":false,"id":"8955434","images":["http://pic3.zhimg.com/467b5f57884e2fc3b2b6ecdb7695aeda.jpg"],"title":"小事 · 时代向前走，我妈也没落后","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8957646","images":["http://pic1.zhimg.com/9d3591924939d41941ba8d066f3e14f0.jpg"],"title":"推荐一些好看的双女主影片","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8957876","images":["http://pic2.zhimg.com/296276bbd779d3b7a50fb6c718f41fdd.jpg"],"title":"「设计可以是一堆公式，也可以是一首诗」","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8958033","images":["http://pic1.zhimg.com/e5c7b92eff70e937559a4099f9c3ce34.jpg"],"title":"双十一 · 为什么我买了一大堆又发现自己其实并不需要？","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8957586","images":["http://pic1.zhimg.com/56b3971c54fb6b190babac694b8324b4.jpg"],"title":"解释人脑的思维是什么，这是高中生能看懂的版本","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8950063","images":["http://pic3.zhimg.com/e3a71e66a93b1311fff6e451e8ae1682.jpg"],"title":"当心，心肌梗死一开始的表现，很可能并不是心脏痛","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8957499","images":["http://pic1.zhimg.com/43b8c6ae94ed56536dbf643ef9173048.jpg"],"title":"知乎好问题 · 取钱时 ATM 故障，处理流程是怎样的？","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8956912","images":["http://pic2.zhimg.com/741cfbbed0018bac98ffd44c3472b219.jpg"],"title":"你是独自生活的「空巢青年」吗？","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8956658","images":["http://pic3.zhimg.com/1cc80a85b3545b2833a920a093270102.jpg"],"title":"喝酒能尝出橡木桶的味道，道理和茶叶蛋差不多","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8949413","images":["http://pic2.zhimg.com/608319503a3e9391cd34b0e4fb5a1fbd.jpg"],"title":"莎士比亚虚掩了一扇门，走进去，是个绚烂多彩的情色世界","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955955","images":["http://pic1.zhimg.com/50235d656ceb979f5f77fe2b5a797dec.jpg"],"title":"大误 · 你对天线宝宝一无所知","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955739","images":["http://pic3.zhimg.com/382e5dc02910f4a0afff6e03ba1f4d5e.jpg"],"title":"火车不再「咣当咣当」，因为铁轨都变「无缝」了","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955549","images":["http://pic3.zhimg.com/f522d68adeaa2b5d219d1e8fd8a6c166.jpg"],"title":"国外名校的申请书一点套路都不给，这是逼我聊人生啊","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955282","images":["http://pic4.zhimg.com/9d9f506a76eac62ce14602c82a5e7c53.jpg"],"title":"「酸碱体质」不靠谱，但食物的酸碱性值得捋一捋","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955123","images":["http://pic4.zhimg.com/2ec7facf5b08c3a8748650192c317fef.jpg"],"title":"觉得「看见了」，其实「没看见」","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955674","images":["http://pic3.zhimg.com/91a6a156bd3e2d7eb08d2e641ec9e4b6.jpg"],"title":"哪些宏观经济学现象有精彩而出乎意料的微观基础？","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955536","images":["http://pic4.zhimg.com/6de71c4db1e2c063ab75d047251579f3.jpg"],"title":"只能想到拍拍照片视频，你也太小看无人机了","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955649","images":["http://pic1.zhimg.com/f75c220fc79a497f9d493efab2736394.jpg"],"title":"「要是老师长得好看，我也不至于成为学渣啊」","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8955516","images":["http://pic4.zhimg.com/d592e063dfcc40069a28b6f554b51283.jpg"],"title":"读读日报 24 小时热门 TOP 5 · 老司机黄阿丽","type":0},
         {"date":"20161107","hasFadedIn":false,"id":"8949380","images":["http://pic4.zhimg.com/831a9880abd57524040e0aedc1a748bf.jpg"]
 */
    @SerializedName("stories")
    private ArrayList<ZhihuDailyItem> stories;
    @SerializedName("top_stories")
    private ArrayList<TopStoryItem> topstories;
    @SerializedName("date")
    private String date;
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<TopStoryItem> getTopstories() {
        return topstories;
    }

    public void setTopstories(ArrayList<TopStoryItem>topstories) {
        this.topstories = topstories;
    }

    public ArrayList<ZhihuDailyItem> getStories() {
        return stories;
    }

    public void setStories(ArrayList<ZhihuDailyItem> stories) {
        this.stories = stories;
    }

}
