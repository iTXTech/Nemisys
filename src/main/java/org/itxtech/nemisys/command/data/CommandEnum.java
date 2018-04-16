package org.itxtech.nemisys.command.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author CreeperFace
 */
@AllArgsConstructor
@Getter
public class CommandEnum {

    private String name;
    private List<String> values;
}
