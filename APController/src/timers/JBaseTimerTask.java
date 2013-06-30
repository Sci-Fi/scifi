/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timers;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Esta classe é a base das classes que representam as tarefas executadas por um Timer.
 *  
 * @author ferolim
 */
public abstract class JBaseTimerTask extends TimerTask
{
    /*
     * Timer que chamou esta tarefa. Seu valor é nulo quando não há
     * interesse em reagendar esta tarefa
     */
    protected Timer m_timer = null;
    
    /**
     * Método que define o Timer que executou esta tarefa. O timer pode ser nulo
     * caso não haja interesse em reagendar a tarefa.
     * 
     * @param timer Timer
     */
    public void setTimer(Timer timer)
    {
        m_timer = timer;
    }
}
