using Microsoft.AspNetCore.Mvc;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.BusinessLogic.Service;
using System;
using System.Threading.Tasks;

namespace SilentManagerApiServer.Controllers
{
    [ApiVersion("1.0")]
    [Route("api/v{version:apiVersion}/users")]
    public class UserController : ControllerBase
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            this._userService = userService;
        }

        [HttpGet]
        public async Task<JsonResult> GetUsers([FromQuery(Name = "userName")] string userName, [FromQuery(Name = "page")] int? pageIndex)
        {
            PagedResult<UserOutDto> users = null;

            if (pageIndex == null)
                pageIndex = 1;

              if (String.IsNullOrEmpty(userName))
              {
                  users = await _userService.GetAllUsers(pageIndex.Value);
              }
              else
              {
                   return DispatchMessage(_userService.GetAllUsersName(userName, pageIndex.Value));
              }

            return new JsonResult(users);
        }
        [HttpGet("{id}")]
        public async Task<JsonResult> GetUser(int id)
        {
            var response = await _userService.GetUserById(id);
            if (response.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(response.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(response.Result);
            
        }

        private JsonResult DispatchMessage <T>(ResultWrapper<T> response)
        {
            JsonResult js = null;
            if (response.Result != null)
            {

                js = new JsonResult(response.Result);
                js.StatusCode = 200;
            }

            else
            {
                js = new JsonResult(response.Errors);
                js.StatusCode = 400;
            }
                return js;
        }
    }
}
