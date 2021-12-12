package real;

/**
 *
 * @author Dũng Trần
 */


public class Effect {
    public int timeStart;
    public int timeLength;
    public int param;
    public EffectData template;
    public long timeRemove;
    
    
    public Effect(int id, int param) {
        this.template = EffectData.entrys.get(id);
        this.param = param;
    }
    
    public Effect(int id, int timeStart, int timeLength, int param) {
        this.template = EffectData.entrys.get(id);
        this.timeStart = timeStart;
        this.timeLength = timeLength;
        this.param = param;
        this.timeRemove = (System.currentTimeMillis()-timeStart)+timeLength;
    }
    
}
