package org.tafia.smartroute.spider.common;

import org.tafia.smartroute.Global;
import org.tafia.smartroute.SmartEntity;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by Dason on 2018/5/20.
 */
public class StoragePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        resultItems.getAll().values().stream()
                .filter(o -> o instanceof SmartEntity)
                .map(o -> (SmartEntity) o)
                .forEach(Global.smartDao()::saveOne);
    }
}
