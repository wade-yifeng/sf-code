package cn.sf.auto.code.client;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.core.DB2DomainDto;
import cn.sf.auto.code.core.DaoManager;
import cn.sf.auto.code.core.Domain2Mapper;
import cn.sf.auto.code.util.CommonUtils;
import cn.sf.auto.code.util.StringConstants;

public class AutoCodeClient {

    public static void autoCode(String propertiesPath){
        PropertiesLoad.init(propertiesPath);
        DB2DomainDto.genDomain();
        DB2DomainDto.genDto();
        Domain2Mapper.genMapper();
        DaoManager.genDao();
        DaoManager.genManager();
        CommonUtils.close(null,null,StringConstants.conn);
    }
}
