package sf.auto.code.client;

import sf.auto.code.config.PropertiesLoad;
import sf.auto.code.core.DB2DomainDto;
import sf.auto.code.core.DaoManager;
import sf.auto.code.core.Domain2Mapper;

/**
 * Created by nijianfeng on 16/12/23.
 */
public class AutoCodeClient {

    public static void autoCode(String propertiesPath){
        PropertiesLoad.init(propertiesPath);
        DB2DomainDto.genDomain();
        DB2DomainDto.genDto();
        Domain2Mapper.genMapper();
        DaoManager.genDao();
        DaoManager.genManager();
    }
}
