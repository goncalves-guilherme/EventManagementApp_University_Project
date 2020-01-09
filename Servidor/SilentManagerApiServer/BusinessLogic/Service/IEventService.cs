using SilentManager.API.BusinessLogic.Dtos;
using SilentManager.API.Models.DB;
using SilentManager.API.Security;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.BusinessLogic.Service
{
    public interface IEventService
    {
        Task<ResultWrapper<CompletedEvent>> CreateEvent(CompletedEvent newEvent, TokenDto token);
        Task<CompletedEvent> GetEvent(int id);
        Task<ResultWrapper<PagedResult<Event>>> GetEventsByUser(int userId, int currentPage);
        Task<ResultWrapper<PagedResult<CompletedEvent>>> GetEvents(double latitude, double longitude, int radius, int currentPage, string category, DateTime? date);
        Task<ResultWrapper<PagedResult<CompletedEvent>>> GetUserEvents(TokenDto token, int? currentPage);
        Task<CompletedEvent> UpdateEvent(CompletedEvent newEvent);
        Task<bool> DeleteEvent(int eventId , TokenDto tk);
    }
}
