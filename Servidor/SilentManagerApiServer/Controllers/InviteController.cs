using Microsoft.AspNetCore.Mvc;
using SilentManager.API.BusinessLogic.Dtos;
using SilentManager.API.BusinessLogic.Service;
using SilentManager.API.BusinessLogic.Validations;
using SilentManager.API.Security;
using System.Threading.Tasks;

namespace SilentManager.API.Controllers
{
    [ApiVersion("1.0")]
    [Route("api/v{version:apiVersion}/invites")]
    public class InviteController : ControllerBase
    {
        private readonly IInviteService _inviteService;

        public InviteController(IInviteService inviteService)
        {
            this._inviteService = inviteService;
        }

        [HttpGet("inviteState")]
        public JsonResult GetInviteState()
        {
            var response = _inviteService.GetInviteState();

            Response.StatusCode = 200;
            return new JsonResult(response.Result);
        }

        [HttpPut]
        public async Task<JsonResult> CreateInvite([FromBody] InviteDto invite, [FromHeader] string token)
        {
            if (invite == null || string.IsNullOrEmpty(token))
            {
                Response.StatusCode = 400;
                return new JsonResult("Bad Request");
            }

            TokenDto tk = new TokenDto {
                AccessToken = token
            };

            var response = await _inviteService.CreateInvite(invite, tk);

            if (response.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(response.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(response.Result);
        }

        [HttpGet("events/{eventId}")]
        public async Task<JsonResult> GetInvites(
            [FromHeader(Name = "Token")] string access_token,
            int eventId,
            [FromQuery(Name = "State")] int? state
            )
        {
            TokenDto tk = new TokenDto
            {
                AccessToken = access_token
            };

            var response = await _inviteService.GetInvites(tk, eventId, state);

            if (response.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(response.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(response.Result);
        }

        [HttpGet("users/me")]
        public async Task<JsonResult> GetMyInvites(
            [FromHeader(Name = "Token")] string token,
            [FromQuery(Name = "State")] int? state
            )
        {
            TokenDto tk = new TokenDto
            {
                AccessToken = token
            };

            var response = await _inviteService.GetInvites(tk, null, state);

            if (response.Errors != null)
            {
                Response.StatusCode = 401;
                return new JsonResult(response.Errors);
            }

            Response.StatusCode = 200;
            return new JsonResult(response.Result);
        }

        [HttpDelete("event/{eventId}/user/{userId}")]
        public async Task<JsonResult> DeleteInvite(int eventId, int userId, [FromHeader] string token)
        {

            InviteDto inviteToDelete = new InviteDto
            {
                EventId = eventId,
                UserId = userId
            };

            TokenDto tk = new TokenDto
            {
                AccessToken = token
            };

            bool isDeleted = await this._inviteService.DeleteInvite(inviteToDelete, tk);

            if (!isDeleted)
            {
                Response.StatusCode = 401;
                return new JsonResult(isDeleted);
            }

            Response.StatusCode = 200;
            return new JsonResult(isDeleted);
        }
    }
}
