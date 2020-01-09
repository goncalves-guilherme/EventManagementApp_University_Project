using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public interface IRepository<TEntity> where TEntity : class
    {
        /// <summary>
        /// This method obtain a TEntity from a db context by its id.
        /// </summary>
        /// <param name="id">The id of the entity TEntity.</param>
        /// <returns>Returns the TEntity</returns>
        TEntity Get(int id);

        /// <summary>
        /// This method obtain a TEntity from a db context by its id asyncronously.
        /// </summary>
        /// <param name="id">The id of the entity TEntity.</param>
        /// <returns>Returns a task with entity TEntity.</returns>
        Task<TEntity> GetAsync(int id);

        /// <summary>
        /// This method will get all TEntity from a db context.
        /// </summary>
        /// <returns>Returns a list of TEntities</returns>
        IEnumerable<TEntity> GetAll();

        /// <summary>
        /// This method will get all TEntity from a db context.
        /// </summary>
        /// <returns>Returns a list of TEntities</returns>
        Task<IEnumerable<TEntity>> GetAllAsync();

        /// <summary>
        /// This
        /// </summary>
        /// <param name="predicate"></param>
        /// <returns></returns>
        IQueryable<TEntity> Find(Expression<Func<TEntity, bool>> predicate);

        void Add(TEntity entity);
        void AddAsync(TEntity entity);
        void AddRange(IEnumerable<TEntity> entities);
        void AddRangeAsync(IEnumerable<TEntity> entities);

        void Remove(TEntity entity);
        void RemoveRange(IEnumerable<TEntity> entities);

        int Save();
        Task<int> SaveAsync();
    }
}
