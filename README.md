# 介绍
现在一般追番要去好几个网站，比如[B站](http://www.bilibili.com/)看日剧、[D站](http://www.scratch.dilidili.wang/)看动漫、[FIX](http://www.fixsub.com/)看美剧
。因此做了一个爬虫，定时去抓取网站番剧的链接，一旦发生更新，服务端就把更新的番剧链接通过邮件发送给需要的用户。

目前支持的网站：
1. [scratch.dilidili](http://www.scratch.dilidili.wang/)
2. [scratch.fix](http://www.fixsub.com/)




## 异常处理

### 网页请求异常

### 默认404

controller.java
``` java
@GetMapping("/404")
public String error() {
    return "base/404";
}
```

web.xml
```xml
<error-page>
    <error-code>404</error-code>
    <location>/404</location>
</error-page>
```



### API异常