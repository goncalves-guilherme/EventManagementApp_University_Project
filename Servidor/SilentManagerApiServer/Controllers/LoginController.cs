using Microsoft.AspNetCore.Mvc;
using SilentManager.API.BusinessLogic.Service;
using SilentManager.API.Security;
using SilentManagerApiServer.BusinessLogic.Dtos;
using System;
using System.Threading.Tasks;

namespace SilentManager.API.Controllers
{
    [ApiVersion("1.0")]
    [Route("api/v{version:apiVersion}")]
    public class LoginController : ControllerBase
    {

        private readonly ILoginService _loginService;

        public LoginController(ILoginService loginService)
        {
            this._loginService = loginService;
        }

        [HttpPost("login")]
        public TokenDto login([FromBody]User _user)
        {

            if (_loginService.ValidateCredentials(_user))
            {
                Response.StatusCode = 200;
                return _loginService.GenerateToken(_user); //guardar base de dados

            }
            else
            {
                Response.StatusCode = 401;
                return null;
            }
        }

        [HttpPost("register")]
        public async Task<JsonResult> Register([FromBody] UserInDto user) 
        {

            var a = await  _loginService.CreateUser(user);
            if (a.Errors != null)
            {
                Response.StatusCode = 401;
                return null;
            }

            User _user = new User
            {
                Email = user.Email,
                Password = user.Password,

            };

            TokenDto _token = _loginService.GenerateToken(_user);         

            Response.StatusCode = 200;
            return new JsonResult(_token);
       }
    }



}

