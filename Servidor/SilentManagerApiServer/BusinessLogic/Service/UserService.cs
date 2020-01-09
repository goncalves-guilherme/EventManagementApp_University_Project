using AutoMapper;
using SilentManager.API.BusinessLogic.Validations;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.DataAccess;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace SilentManagerApiServer.BusinessLogic.Service
{
    public class UserService : IUserService
    {
        private readonly IUnitOfWork _unitOfWork;
        private readonly UserValidations userValid;

        public UserService(IUnitOfWork unitOfWork)
        {
            this._unitOfWork = unitOfWork;
            this.userValid = new UserValidations();
        }

        public async Task<PagedResult<UserOutDto>> GetAllUsers(int currentPage)
        {
            var allUsers = await _unitOfWork.UserRepo.GetAllAsync();
            var pageSize = 25;

            var pagedUsers = allUsers
                .Skip((currentPage - 1) * pageSize)
                .Take(pageSize);

            var pagedUsersDto = Mapper.Map<IEnumerable<UserOutDto>>(pagedUsers);
            return new PagedResult<UserOutDto>(currentPage, allUsers.Count(), pageSize, pagedUsersDto);
        }

        public ResultWrapper<PagedResult<UserOutDto>> GetAllUsersName(string name, int currentPage)
        {
            IEnumerable<ErrorResult> errors = userValid.ValidateUsername(name);
            if (errors.Count() > 0)
                return new ResultWrapper<PagedResult<UserOutDto>>(null, errors);

            IQueryable<User> users = _unitOfWork.UserRepo.Find(user => user.Name.Contains(name));
            int pagesCount = users.Count();
            int pageSize = 25;

            users = users
                .Skip((currentPage - 1) * pageSize)
                .Take(25);

            var usersDto = Mapper.Map<IEnumerable<UserOutDto>>(users);
            PagedResult<UserOutDto> page = new PagedResult<UserOutDto>(currentPage, pagesCount, pageSize, usersDto);
            return new ResultWrapper<PagedResult<UserOutDto>>(page, null);
        }


        public async Task<ResultWrapper<UserOutDto>> GetUserById(int id)
        {
            userValid.ValidateUserEmail("AAAA@hotmail.com");
            var invalidId = userValid.ValidateGetUserById(id);
            if (invalidId.Count() > 0)
                return new ResultWrapper<UserOutDto>(null, invalidId);

            var user = await _unitOfWork.UserRepo.GetAsync(id);
            if (user == null)
            {
                List<ErrorResult> errors = new List<ErrorResult>();
                errors.Add(new ErrorResult(1, "The user with id " + id + " does not exits."));
                return new ResultWrapper<UserOutDto>(null, errors);
            }

            return new ResultWrapper<UserOutDto>(Mapper.Map<UserOutDto>(user), null);
        }

        public async Task<bool> UpdateUser(UserOutDto user)
        {
            var userToUpdate = await _unitOfWork.UserRepo.GetAsync(user.UserId);
            userToUpdate.Name = user.Name;
            userToUpdate.Email = user.Email;
            return await _unitOfWork.UserRepo.SaveAsync() > 0;
        }

    }
}
