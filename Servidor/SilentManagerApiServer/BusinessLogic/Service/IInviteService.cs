using Microsoft.AspNetCore.Mvc;
using SilentManager.API.BusinessLogic.Dtos;
using SilentManager.API.Security;
using SilentManagerApiServer.BusinessLogic.Dtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Service
{
    public interface IInviteService
    {
        Task<IEnumerable<InviteStateDto>> GetInviteState();
        Task<ResultWrapper<InviteOutDto>> CreateInvite(InviteDto newInvite, TokenDto token);
        Task<ResultWrapper<IEnumerable<InviteOutDto>>> GetInvites(TokenDto token, int? eventId, int? state);
        Task<bool> DeleteInvite(InviteDto inviteToDelete, TokenDto token);
    }
}
