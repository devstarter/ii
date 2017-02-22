package org.ayfaar.app.services.sysLogs;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.model.SysLog;
import org.ayfaar.app.model.User;
import org.ayfaar.app.utils.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SysLogServiceImpl implements SysLogService {
    CommonDao commonDao;
    CurrentUserProvider currentUserProvider;

    @Autowired
    public SysLogServiceImpl(CommonDao commonDao, CurrentUserProvider currentUserProvider) {
        this.commonDao = commonDao;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public List<SysLog> getAll() {
        return commonDao.getAll(SysLog.class);
    }

    @Override
    public SysLog save(SysLogEvent event) {
        return commonDao.save(toSysLog(event));
    }

    private SysLog toSysLog(SysLogEvent event) {
        Optional<User> userOptional = currentUserProvider.get();
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }

        SysLog sysLog = new SysLog();
        sysLog.setUser(user);
        sysLog.setDate(new Date());
        sysLog.setLogger(event.getSource().getClass().getName());
        sysLog.setMessage(event.getMessage());
        sysLog.setLevel(event.getLevel());

        return sysLog;
    }
}
