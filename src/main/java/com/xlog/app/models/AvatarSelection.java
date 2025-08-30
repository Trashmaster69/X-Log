
package com.xlog.app.models;
public class AvatarSelection {
    public enum BodyColor { YELLOW, BLUE, RED }
    public enum EyeType { ANGRY, PEACE, NORMAL, ANNOYED }
    public enum Displayable { NONE, FOOTBALL, SUNGLASSES, STAFF }
    private BodyColor body=BodyColor.BLUE; private EyeType eyes=EyeType.NORMAL; private Displayable display=Displayable.NONE;
    public AvatarSelection(){} public AvatarSelection(BodyColor b, EyeType e, Displayable d){ body=b; eyes=e; display=d; }
    public BodyColor getBody(){ return body; } public EyeType getEyes(){ return eyes; } public Displayable getDisplay(){ return display; }
    public void setBody(BodyColor c){ body=c; } public void setEyes(EyeType e){ eyes=e; } public void setDisplay(Displayable d){ display=d; }
}
