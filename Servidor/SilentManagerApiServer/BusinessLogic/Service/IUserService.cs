
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.BusinessLogic.Service
{
    public interface IUserService
    {
        Task<PagedResult<UserOutDto>> GetAllUsers(int currentPage);
        ResultWrapper<PagedResult<UserOutDto>> GetAllUsersName(string name, int currentPage);
        Task<ResultWrapper<UserOutDto>> GetUserById(int id);
        Task<bool> UpdateUser(UserOutDto user);

        //Task<PagedResult<PersonalSettingsDto>> GetPersonalSettings(int userId, int currentPage);
        //Task<ResultWrapper<PersonalSettings>> CreatePersonalSettings(PersonalSettingsDto newPersonalSettings);
    }
}
