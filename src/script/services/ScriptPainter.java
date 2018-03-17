package script.services;

import org.omg.CORBA.INVALID_TRANSACTION;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.canvas.paint.Painter;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import script.EasyAlch;
import script.util.Timer;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class ScriptPainter implements Painter {

    private final static NumberFormat k = new DecimalFormat("###,###,###");
    private final static Color OPAQUE_BLACK = new Color(0, 0, 0, 160);
    private final static Color OPAQUE_BLACK2 = new Color(0, 0, 0, 180);
    private final static Color GREEN = new Color(85, 110, 36, 180);
    private static final Color BLUE = new Color(0, 0, 255, 180);
    private static final int HIGH_ALCH_XP = 65;

    private final String _version;

    private final EasyAlch _easyAlch;
    private final Script _ctx;
    private final SkillTracker.Tracker _tracker;


    public ScriptPainter(Script ctx, EasyAlch easyAlch) {
        _easyAlch = easyAlch;
        _ctx = ctx;
        ctx.getBot().getPainters().add(this);
        _version = "v" + ctx.getVersion();
        _tracker =new SkillTracker(ctx.getSkills()).getSkill(Skill.MAGIC);
    }

    @Override
    public void onPaint(Graphics2D g) {
        drawMouse(g);
        drawRectangle(g, BLUE, _easyAlch.AlchItemLocation, false);
        drawRectangle(g, GREEN, _easyAlch.HighAlchSpellLocation, false);
        drawRectangle(g, GREEN, _easyAlch.AlchLocation, true);

        _ctx.getExperienceTracker().getTimeToLevel(Skill.MAGIC);
        _ctx.getExperienceTracker().getElapsed(Skill.MAGIC);

        final String line1 = _version + "  Run Time: " + _tracker.formattedTimeElapsed() + "    Alchs : " + k.format(_tracker.gainedXp() / HIGH_ALCH_XP ) + " - " + k.format(_tracker.gainedXpPerHour() / HIGH_ALCH_XP) + "/H";
        final String line2 = "Magic (" + _tracker.currentLevel() + ")" + (_tracker.gainedLevels() == 0 ? "" : "+" + (_tracker.gainedLevels()))
                + " - " + formatter(_tracker.gainedXp()) + " - " + formatter(_tracker.gainedXpPerHour()) + "/H - TTL: " + _tracker.formattedTimeTillNextLevel();

        int yComp = 345, xComp = 20;
        g.setColor(OPAQUE_BLACK);
        g.fill3DRect(0, yComp, 300, 45, true);
        g.setFont(new Font("Gayatri", 0, 12));
        shadowText(g, line1, xComp, yComp += 18);
        g.setColor(Color.BLACK);
        g.draw3DRect(xComp - 5, yComp + 5, 270, 17, true);
        g.setColor(OPAQUE_BLACK2);
        g.fill3DRect(xComp - 5, yComp + 5, 270, 17, true);
        g.setColor(BLUE);
        g.fillRect(xComp - 4, yComp + 6, (int) (268 * _tracker.percentTillNextLevel()), 15);
        shadowText(g, line2, xComp, yComp += 18);
    }

    private void drawRectangle(Graphics2D g, Color color, Rectangle r, boolean use3d)
    {
        if(r == null)
            return;
        g.setColor(color);
        if(use3d)
            g.fill3DRect(r.x, r.y, r.width, r.height, true);
        else
            g.drawRect(r.x, r.y, r.width, r.height);
    }

    private void drawMouse(Graphics g) {
        g.setColor(Color.RED);
        final Point m = _ctx.getMouse().getPosition();
        g.drawLine(m.x - 5, m.y + 5, m.x + 5, m.y - 5);
        g.drawLine(m.x - 5, m.y - 5, m.x + 5, m.y + 5);
    }

    private void shadowText(Graphics g, final String line, final int x, final int y) {
        g.setColor(Color.BLACK);
        g.drawString(line, x + 1, y + 1);
        g.setColor(Color.WHITE);
        g.drawString(line, x, y);
    }

    private String formatter(final int num) {
        return num / 1000 + "." + (num % 1000) / 100 + "K";
    }
}
