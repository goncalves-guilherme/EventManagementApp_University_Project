using SilentManager.API.Security;
using SilentManagerApiServer.BusinessLogic.Dtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Service
{
    public interface ILoginService
    {
        bool ValidateCredentials(User user);
        TokenDto GenerateToken(User user);
        Task<ResultWrapper<UserOutDto>> CreateUser(UserInDto user);
    }
}
