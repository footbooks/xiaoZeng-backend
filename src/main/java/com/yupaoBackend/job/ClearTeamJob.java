package com.yupaoBackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupaoBackend.entity.Team;
import com.yupaoBackend.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ClearTeamJob {
    @Resource
    private TeamService teamService;
    @Scheduled(cron = "0 0 0 * * *")
    public void doClearTeam(){
        //获取当前系统时间
        Date date = new Date();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teamList = teamService.list(queryWrapper);
        //获取已经过期的队伍
        List<Team> needTeamList = teamList.stream()
                .filter(team -> {
                    Date expireTime = team.getExpireTime();
                    return date.after(expireTime);
                })
                .collect(Collectors.toList());
        //批量删除已经过期的队伍
        teamService.removeBatchByIds(needTeamList);
    }
}
