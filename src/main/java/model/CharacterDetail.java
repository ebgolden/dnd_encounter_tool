package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import java.io.File;

@Builder
@Data
@AllArgsConstructor
@ToString(includeFieldNames=false)
public class CharacterDetail {
    private String characterName;
    private double initiative;
    private int initiativeBonus, armorClass, hitPoints;
    @ToString.Exclude
    private File file;
}