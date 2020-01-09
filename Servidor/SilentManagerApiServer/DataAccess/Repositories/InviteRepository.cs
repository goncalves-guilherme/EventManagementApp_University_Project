using SilentManager.API.BusinessLogic.Dtos;
using SilentManager.API.Models.DB;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public class InviteRepository : Repository<Invite>, IInviteRepository
    {
        private readonly SilentManagerContext dbContext;
        public InviteRepository(SilentManagerContext dbContext)
            : base(dbContext)
        {
            this.dbContext = dbContext;
        }

        public IQueryable<InviteState> GetAllInviteState()
        {
            return
             from _inviteState in this.dbContext.InviteState
             select new InviteState
             {
                 InviteId = _inviteState.InviteId,
                 State = _inviteState.State,
                 Invite = null
             };
        }


        public IQueryable<InviteOutDto> GetInvites(int? userId, int? eventId, int? state)
        {
            var invites = from _invite in this.dbContext.Invite
                          join _user in this.dbContext.User
                          on _invite.UserId equals _user.UserId
                          join _event in this.dbContext.Event
                          on _invite.EventId equals _event.EventId
                          join _location in this.dbContext.Location
                          on _event.Location equals _location.LocationId
                          join ev_cate in this.dbContext.EventHasCategories
                          on _event.EventId equals ev_cate.EventId
                          join cate in this.dbContext.Category
                          on ev_cate.CategoryId equals cate.CategoryId
                          select new InviteOutDto
                          {
                              User = new UserOutDto
                              {
                                  UserId = _user.UserId,
                                  Name = _user.Name,
                                  Email = _user.Email
                              },
                              Event = new CompletedEvent
                              {
                                  Name = _event.Name,
                                  EventId = _event.EventId,
                                  Author = null,
                                  Description = _event.Description,
                                  EndDate = _event.EndDate,
                                  StartDate = _event.StartDate,
                                  Radius = _event.Radius,
                                  State = _event.State,
                                  Category = cate.Category1,
                                  Location = new CompletedLocation
                                  {
                                      Address = _location.Address,
                                      LocationId = _location.LocationId,
                                      Latitude = _location.Latitude,
                                      Longitude = _location.Longitude
                                  }
                              },
                              State = _invite.State
                          };

            return invites.Where(_in => (userId == null || _in.User.UserId == userId) &&
                                        (eventId == null || _in.Event.EventId == eventId) &&
                                        (state == null || _in.State == state)
                                        );

        }
    }
}
