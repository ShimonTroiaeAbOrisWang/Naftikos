package com.java.wanghongjian_and_liuxiao;

import com.java.wanghongjian_and_liuxiao.MongoDB;
import com.java.wanghongjian_and_liuxiao.data.Result;
import com.java.wanghongjian_and_liuxiao.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            int state = MongoDB.login(username, password);
            if (state == 1 || state == 2){
                LoggedInUser User = new LoggedInUser(username, username);
                return new Result.Success<>(User);
            }else{
                return new Result.Error(new IOException("Error logging in"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
