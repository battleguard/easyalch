package script.services;

import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.ui.Skill;
import script.util.Timer;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SkillTracker {
    private final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);
    private final Map<Skill, Tracker> _skillMap;
    private final Skills _skills;

    public SkillTracker(Skills skills) {
        _skills = skills;
        _skillMap = Arrays.stream(Skill.values()).map(Tracker::new).collect(Collectors.toMap(t -> t._skill, t -> t));
    }

    public Tracker getSkill(Skill skill)
    {
        return _skillMap.get(skill);
    }

    public class Tracker {

        public final Skill _skill;
        private final long _startTime;
        private final int _startLevel;
        private final int _startXp;


        public Tracker(Skill skill) {
            _skill = skill;
            _startTime = System.currentTimeMillis();
            _startLevel = _skills.getStatic(skill());
            _startXp = _skills.getExperience(skill());
        }

        public double percentTillNextLevel()
        {
            return (double) (currentXp() - experienceForLevel(currentLevel())) / (double) (experienceForLevel(currentLevel() + 1) - experienceForLevel(currentLevel()));
        }

        public Skill skill()
        {
            return _skill;
        }

        public int experienceForLevel(int level)
        {
            return _skills.getExperienceForLevel(level);
        }

        public int experienceTillNextLevel()
        {
            return experienceForLevel(currentLevel() + 1) - currentXp();
        }

        public int currentXp()
        {
            return _skills.getExperience(skill());
        }

        public int gainedXp()
        {
            return currentXp() - _startXp;
        }

        public int gainedXpPerHour()
        {
            return (int)(gainedXp() * (ONE_HOUR / timeElapsed()));
        }

        public int currentLevel()
        {
            return _skills.getStatic(skill());
        }

        public int gainedLevels()
        {
            return currentLevel() - _startLevel;
        }

        public long timeElapsed()
        {
            return System.currentTimeMillis() - _startTime;
        }

        public String formattedTimeElapsed()
        {
            return Timer.prettyTime(timeElapsed());
        }

        public long timeTillNextLevel()
        {
            return gainedXp() == 0 ? 0 : ((long)experienceTillNextLevel() * ONE_HOUR) / (long)gainedXpPerHour();
        }

        public String formattedTimeTillNextLevel()
        {
            return Timer.prettyTime(timeTillNextLevel());
        }
    }
}
