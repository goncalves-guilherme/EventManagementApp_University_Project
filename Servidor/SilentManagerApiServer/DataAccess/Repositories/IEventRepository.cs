using SilentManager.API.Models.DB;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public interface IEventRepository : IRepository<Event>
    {
        IQueryable<Location> getLocationId(int id);
        Task<IQueryable<CompletedEvent>> GetEventsByDistanceRangeAsync(double latitute, double longitude, int radius, string cate, DateTime? date);
        Task<IQueryable<CompletedEvent>> GetEventsByUserAsync(int userId);
    }
}
