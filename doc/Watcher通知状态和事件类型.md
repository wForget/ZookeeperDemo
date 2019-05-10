<table border="0" cellpadding="0" cellspacing="0" width="841">
    <colgroup>
        <col width="176">
        <col width="146">
        <col width="121">
        <col width="287">
        <col width="111">
    </colgroup>
    <tbody>
        <tr>
            <td>KeeperState</td>
            <td>EventType&nbsp;</td>
            <td>触发条件&nbsp;</td>
            <td>说明&nbsp;</td>
            <td>操作</td>
        </tr>
        <tr>
            <td rowspan="5">SyncConnected</td>
            <td>None</td>
            <td>客户端与服务端<br> 成功建立连接</td>
            <td rowspan="5">此时客户端和服务器处于连接状态&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>NodeCreated</td>
            <td>Watcher 监听的<br> 对应数据节点被<br> 创建</td>
            <td>Create</td>
        </tr>
        <tr>
            <td>NodeDeleted</td>
            <td>Watcher 监听的<br> 对应数据节点被<br> 删除</td>
            <td>Delete/znode</td>
        </tr>
        <tr>
            <td>NodeDataChanged</td>
            <td>Watcher 监听的<br> 对应数据节点的<br> 数据内容发生变<br> 更</td>
            <td>setDate/znode</td>
        </tr>
        <tr>
            <td>NodeChildChanged</td>
            <td>Wather 监听的<br> 对应数据节点的<br> 子节点列表发生<br> 变更</td>
            <td>Create/child</td>
        </tr>
        <tr>
            <td>Disconnected</td>
            <td>None</td>
            <td>客户端与<br> ZooKeeper 服务<br> 器断开连接</td>
            <td>此时客户端和服<br> 务器处于断开连<br> 接状态</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>Expired</td>
            <td>None</td>
            <td>会话超时&nbsp;</td>
            <td>此时客户端会话失效，通常同时也会受到<br> SessionExpiredException 异常</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>AuthFailed--4</td>
            <td>None</td>
            <td>通常有两种情<br> 况， 1：使用错<br> 误的 schema 进<br> 行权限检查 2：<br> SASL 权限检查<br> 失败</td>
            <td>通常同时也会收到AuthFailedException 异常</td>
            <td>&nbsp;</td>
        </tr>
    </tbody>
</table>