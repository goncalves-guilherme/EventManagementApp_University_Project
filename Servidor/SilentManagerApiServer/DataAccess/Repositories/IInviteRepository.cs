using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using SilentManagerApiServer.Models.DB;
using SilentManagerApiServer.DataAccess;
using SilentManager.API.BusinessLogic.Dtos;

namespace SilentManagerApiServer.DataAccess
{
    public interface IInviteRepository : IRepository<Invite>
    {
        IQueryable<InviteState> GetAllInviteState();
        IQueryable<InviteOutDto> GetInvites(int? userId, int? eventId, int? state);

    }
}
