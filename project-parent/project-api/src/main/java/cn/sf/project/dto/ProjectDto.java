package cn.sf.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Data
@NoArgsConstructor
public class ProjectDto implements Serializable {

    private Long id;
    private String projectName;

}
