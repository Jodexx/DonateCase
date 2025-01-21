package com.jodexindustries.donatecase.api.armorstand;

import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Setter
@Getter
@ConfigSerializable
public class ArmorStandEulerAngle {
    @Setting("Head")
    private EulerAngle head;
    @Setting("Body")
    private EulerAngle body;
    @Setting("RightArm")
    private EulerAngle rightArm;
    @Setting("LeftArm")
    private EulerAngle leftArm;
    @Setting("RightLeg")
    private EulerAngle rightLeg;
    @Setting("LeftLeg")
    private EulerAngle leftLeg;

    public ArmorStandEulerAngle(EulerAngle head, EulerAngle body, EulerAngle rightArm, EulerAngle leftArm, EulerAngle rightLeg, EulerAngle leftLeg) {
        this.head = head;
        this.body = body;
        this.rightArm = rightArm;
        this.leftArm = leftArm;
        this.rightLeg = rightLeg;
        this.leftLeg = leftLeg;
    }

}
