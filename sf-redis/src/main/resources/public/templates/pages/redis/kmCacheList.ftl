<html>
<head>
  <title>cache区域块列表</title>
</head>
<body>
<#if cacheInfos?exists>
<br/><font color="red">缓存类型:  jedis key HashMap</font>
<br/>
<#list cacheInfos as keys>
    <br/>
    Cache Map Key:${keys.key}
    <br/>
    缓存时间:${keys.expireTime}秒         剩余时间:${keys.ttl}秒
<br/>
    拥有元素个数:${keys.count}
    <a href="${keys.url}&redirect=true<#if level>&level=${level?c}</#if>">删除</a>
    <br/>
    ----------------------------------------------------------------------------
    <#if level>
      <#list keys.fieldInfos as fields>
       <br/>
       Cache Map Field:${fields.field}
       <a href="${fields.url}&redirect=true<#if level>&level=${level?c}</#if>">删除</a>
       <br/>
       </#list>
     <p>
     --------------------------------------------------------------------------------------------------------------------------------------------------------
    </#if>
</#list> 
</#if>

<br/>

</body>
</html> 